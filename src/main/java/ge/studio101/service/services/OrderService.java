package ge.studio101.service.services;

import ge.studio101.service.delivery.DeliveryProvider;
import ge.studio101.service.dto.CartItemDTO;
import ge.studio101.service.dto.ContactInfoDTO;
import ge.studio101.service.dto.OrderPayload;
import ge.studio101.service.dto.OrderResponseDTO;
import ge.studio101.service.models.*;
import ge.studio101.service.repositories.*;
import ge.studio101.service.services.delivery.DeliveryOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final GooglePayService googlePayService;
    private final Optional<EmailNotificationService> emailNotificationService;
    private final DeliveryOrderService deliveryOrderService;

    @Transactional
    public OrderResponseDTO createOrder(OrderPayload payload) {
        validatePayload(payload);

        BigDecimal calculatedTotal = calculateTotal(payload.getItems());
        if (payload.getTotal() != null && payload.getTotal().compareTo(calculatedTotal) != 0) {
            log.warn("Сумма заказа из клиента ({}) не совпадает с рассчитанной ({})", payload.getTotal(), calculatedTotal);
        }

        if (payload.getPaymentToken() != null && !payload.getPaymentToken().isBlank()) {
            googlePayService.processPaymentToken(payload.getPaymentToken(), calculatedTotal);
        }

        CustomerOrder order = CustomerOrder.builder()
                .userId(resolveUserId(payload.getUserId()))
                .contact(mapContact(payload.getContact()))
                .deliveryOption(payload.getDeliveryOption())
                .deliveryProvider(resolveProvider(payload.getDeliveryProvider()))
                .notes(payload.getNotes())
                .total(calculatedTotal)
                .paymentToken(payload.getPaymentToken())
                .emailNotification(Boolean.TRUE.equals(payload.getEmailNotification()))
                .createdAt(OffsetDateTime.now())
                .status("Создан")
                .build();

        List<OrderItem> items = new ArrayList<>(payload.getItems().stream()
                .map(itemDto -> toOrderItem(itemDto, order))
                .toList());

        order.setItems(items);

        CustomerOrder saved = orderRepository.save(order);
        if (saved.getOrderNumber() == null) {
            saved.setOrderNumber("#S1-" + saved.getId());
        }
        if (saved.getStatus() == null) {
            saved.setStatus("Создан");
        }
        orderRepository.save(saved);
        deliveryOrderService.createDeliveryOrder(saved)
                .ifPresent(info -> {
                    saved.setTrackingCode(info.getTrackingCode());
                    saved.setTrackingUuid(info.getUuid());
                    saved.setDeliveryStatus(info.getCurrentStatus());
                    saved.setDeliveryLabelUrl(info.getLabelUrl());
                    orderRepository.save(saved);
                });
        orderItemRepository.saveAll(items);

        triggerEmail(saved);

        return OrderResponseDTO.builder()
                .orderId(saved.getId())
                .total(saved.getTotal())
                .status("created")
                .build();
    }

    private void validatePayload(OrderPayload payload) {
        if (payload == null || payload.getItems() == null || payload.getItems().isEmpty()) {
            throw new IllegalArgumentException("Состав заказа пуст");
        }
        payload.getItems().forEach(this::validateItem);
        if (payload.getContact() == null || payload.getContact().getEmail() == null) {
            throw new IllegalArgumentException("Контактные данные обязательны");
        }
    }

    private void validateItem(CartItemDTO itemDTO) {
        if (itemDTO.getItemId() == null || itemDTO.getQuantity() == null || itemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Неверное количество товара");
        }
        Item item = itemRepository.findById(itemDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + itemDTO.getItemId()));
        Color color = colorRepository.findByItemIdAndName(item.getId(), itemDTO.getColorName())
                .orElseThrow(() -> new IllegalArgumentException("Цвет не найден: " + itemDTO.getColorName()));

        Size size = null;
        if (itemDTO.getSizeName() != null) {
            size = sizeRepository.findByName(itemDTO.getSizeName())
                    .orElseThrow(() -> new IllegalArgumentException("Размер не найден: " + itemDTO.getSizeName()));
        }

        inventoryRepository.findByColor_IdAndSize_Id(color.getId(), size != null ? size.getId() : null)
                .orElseThrow(() -> new IllegalArgumentException("Нет наличия для выбранных параметров"));
    }

    private BigDecimal calculateTotal(List<CartItemDTO> items) {
        return items.stream()
                .map(itemDto -> {
                    Item item = itemRepository.findById(itemDto.getItemId())
                            .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + itemDto.getItemId()));
                    return item.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CustomerContact mapContact(ContactInfoDTO dto) {
        if (dto == null) {
            return null;
        }
        String fullName = dto.getFullName();
        if ((fullName == null || fullName.isBlank()) && dto.getFirstName() != null) {
            fullName = dto.getFirstName() + (dto.getLastName() != null ? " " + dto.getLastName() : "");
        }

        String addressLine = dto.getAddressLine();
        if ((addressLine == null || addressLine.isBlank()) && dto.getAddressLine1() != null) {
            addressLine = dto.getAddressLine1() + (dto.getAddressLine2() != null && !dto.getAddressLine2().isBlank() ? ", " + dto.getAddressLine2() : "");
        }

        return new CustomerContact(
                dto.getFirstName(),
                dto.getLastName(),
                fullName,
                dto.getPhone(),
                dto.getEmail(),
                addressLine,
                dto.getAddressLine1(),
                dto.getAddressLine2(),
                dto.getCity(),
                dto.getPostalCode(),
                dto.getMunicipality(),
                dto.getRegion(),
                dto.getCountry()
        );
    }

    private OrderItem toOrderItem(CartItemDTO dto, CustomerOrder order) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + dto.getItemId()));
        Color color = colorRepository.findByItemIdAndName(item.getId(), dto.getColorName())
                .orElseThrow(() -> new IllegalArgumentException("Цвет не найден: " + dto.getColorName()));
        Size size = null;
        if (dto.getSizeName() != null) {
            size = sizeRepository.findByName(dto.getSizeName())
                    .orElseThrow(() -> new IllegalArgumentException("Размер не найден: " + dto.getSizeName()));
        }

        return OrderItem.builder()
                .order(order)
                .item(item)
                .color(color)
                .size(size)
                .itemName(item.getName())
                .colorName(color.getName())
                .sizeName(size != null ? size.getName() : null)
                .quantity(dto.getQuantity())
                .price(item.getPrice())
                .build();
    }

    private Long resolveUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .map(u -> userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    private void triggerEmail(CustomerOrder order) {
        if (order.isEmailNotification() && order.getContact() != null && order.getContact().getEmail() != null) {
            String subject = "Ваш заказ №" + order.getId() + " подтверждён";
            String body = "Спасибо за заказ на сумму " + order.getTotal() + " с опцией доставки " + order.getDeliveryOption();
            emailNotificationService.ifPresent(service -> {
                try {
                    service.sendOrderConfirmation(order.getContact().getEmail(), subject, body);
                } catch (Exception ex) {
                    log.warn("Ошибка при отправке подтверждения: {}", ex.getMessage());
                }
            });
        }
    }

    private DeliveryProvider resolveProvider(String provider) {
        if (provider == null || provider.isBlank()) {
            return DeliveryProvider.INTERNAL;
        }
        try {
            return DeliveryProvider.valueOf(provider);
        } catch (IllegalArgumentException ex) {
            log.warn("Неизвестный провайдер доставки {}, используем INTERNAL", provider);
            return DeliveryProvider.INTERNAL;
        }
    }
}
