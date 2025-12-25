package ge.studio101.service.delivery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DeliveryServiceFactory {

    private final Map<DeliveryProvider, DeliveryService> services;

    public DeliveryServiceFactory(List<DeliveryService> candidates,
                                  @Value("${delivery.provider.enabled:INTERNAL}") String enabledProviders) {
        Set<DeliveryProvider> enabled = parseEnabled(enabledProviders);
        this.services = candidates.stream()
                .filter(service -> enabled.contains(service.getProvider()))
                .collect(Collectors.toMap(DeliveryService::getProvider, service -> service));
        log.info("Доступные службы доставки: {}", services.keySet());
    }

    public Optional<DeliveryService> getService(DeliveryProvider provider) {
        return Optional.ofNullable(services.get(provider));
    }

    private Set<DeliveryProvider> parseEnabled(String enabledProviders) {
        if (enabledProviders == null || enabledProviders.isBlank()) {
            return EnumSet.of(DeliveryProvider.INTERNAL);
        }
        String[] parts = enabledProviders.split(",");
        Set<DeliveryProvider> result = EnumSet.noneOf(DeliveryProvider.class);
        for (String part : parts) {
            try {
                result.add(DeliveryProvider.valueOf(part.trim()));
            } catch (IllegalArgumentException ex) {
                log.warn("Неизвестный поставщик доставки в конфигурации: {}", part);
            }
        }
        if (result.isEmpty()) {
            result.add(DeliveryProvider.INTERNAL);
        }
        return result;
    }
}
