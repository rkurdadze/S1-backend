package ge.studio101.service.services.delivery;

import ge.studio101.service.delivery.DeliveryProvider;
import ge.studio101.service.delivery.DeliveryServiceFactory;
import ge.studio101.service.dto.delivery.CreateOrderRequest;
import ge.studio101.service.dto.delivery.OrderInfoDTO;
import ge.studio101.service.models.CustomerContact;
import ge.studio101.service.models.CustomerOrder;
import ge.studio101.service.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryOrderService {

    private final DeliveryServiceFactory deliveryServiceFactory;
    private final OrderRepository orderRepository;

    public Optional<OrderInfoDTO> createDeliveryOrder(CustomerOrder order) {
        DeliveryProvider provider = resolveProvider(order);
        return deliveryServiceFactory.getService(provider)
                .map(service -> service.createOrder(buildRequest(order)));
    }

    @Transactional
    public void refreshStatus(CustomerOrder order) {
        DeliveryProvider provider = resolveProvider(order);
        deliveryServiceFactory.getService(provider)
                .flatMap(service -> Optional.ofNullable(service.getOrder(resolveIdentifier(order))))
                .ifPresent(info -> {
                    order.setDeliveryStatus(info.getCurrentStatus());
                    order.setTrackingCode(info.getTrackingCode());
                    order.setTrackingUuid(info.getUuid());
                    orderRepository.save(order);
                });
    }

    private DeliveryProvider resolveProvider(CustomerOrder order) {
        return order.getDeliveryProvider() != null ? order.getDeliveryProvider() : DeliveryProvider.INTERNAL;
    }

    private String resolveIdentifier(CustomerOrder order) {
        if (order.getTrackingCode() != null && !order.getTrackingCode().isBlank()) {
            return order.getTrackingCode();
        }
        if (order.getTrackingUuid() != null && !order.getTrackingUuid().isBlank()) {
            return order.getTrackingUuid();
        }
        return order.getId() != null ? order.getId().toString() : "";
    }

    private CreateOrderRequest buildRequest(CustomerOrder order) {
        CreateOrderRequest request = new CreateOrderRequest();
        CustomerContact contact = order.getContact();
        request.setPickupMethod("COURIER");
        request.setDeliveryMethod("COURIER");
        if (contact != null) {
            request.setReceiverName(contact.getFullName());
            request.setReceiverPhoneNumber(contact.getPhone());
            request.setDescription(contact.getAddressLine());
        }
        request.setItemsQuantity(order.getItems() != null ? order.getItems().size() : 1);
        request.setWeight(order.getTotal() != null ? order.getTotal() : BigDecimal.valueOf(0.5));
        request.setFragile(Boolean.FALSE);
        request.setInsured(Boolean.FALSE);
        request.setPayer("SENDER");
        request.setPaymentType("CASHLESS");
        return request;
    }
}
