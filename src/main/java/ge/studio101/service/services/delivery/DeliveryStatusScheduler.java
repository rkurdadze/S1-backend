package ge.studio101.service.services.delivery;

import ge.studio101.service.delivery.DeliveryProvider;
import ge.studio101.service.models.CustomerOrder;
import ge.studio101.service.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryStatusScheduler {

    private final OrderRepository orderRepository;
    private final DeliveryOrderService deliveryOrderService;

    @Scheduled(fixedDelayString = "${delivery.status.refresh-interval:600000}")
    public void refreshStatuses() {
        List<CustomerOrder> orders = orderRepository.findAll();
        orders.stream()
                .filter(order -> order.getDeliveryProvider() == DeliveryProvider.TRACKINGS_GE)
                .filter(order -> order.getTrackingCode() != null || order.getTrackingUuid() != null)
                .forEach(order -> {
                    try {
                        deliveryOrderService.refreshStatus(order);
                    } catch (Exception ex) {
                        log.warn("Не удалось обновить статус заказа {}: {}", order.getId(), ex.getMessage());
                    }
                });
    }
}
