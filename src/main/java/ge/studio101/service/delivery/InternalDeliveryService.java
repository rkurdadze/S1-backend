package ge.studio101.service.delivery;

import ge.studio101.service.dto.delivery.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class InternalDeliveryService implements DeliveryService {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public DeliveryProvider getProvider() {
        return DeliveryProvider.INTERNAL;
    }

    @Override
    public List<RegionDTO> getRegions() {
        return Collections.emptyList();
    }

    @Override
    public List<CityDTO> getCities() {
        return Collections.emptyList();
    }

    @Override
    public List<CityDTO> getCitiesByRegion(int regionId) {
        return Collections.emptyList();
    }

    @Override
    public List<PudoCityDTO> getPudoCities() {
        return Collections.emptyList();
    }

    @Override
    public List<PudoDTO> getPudosByCity(int cityId) {
        return Collections.emptyList();
    }

    @Override
    public List<AddressDTO> listAddresses(AddressType type) {
        return Collections.emptyList();
    }

    @Override
    public AddressDTO createAddress(CreateAddressRequest req) {
        AddressDTO dto = new AddressDTO();
        dto.setId(counter.incrementAndGet());
        dto.setType(req.getType());
        dto.setAddress(req.getAddress());
        dto.setCreatedAt(OffsetDateTime.now());
        return dto;
    }

    @Override
    public AddressDTO updateAddress(int id, UpdateAddressRequest req) {
        AddressDTO dto = new AddressDTO();
        dto.setId(id);
        dto.setType(req.getType());
        dto.setAddress(req.getAddress());
        dto.setCreatedAt(OffsetDateTime.now());
        return dto;
    }

    @Override
    public void deleteAddress(int id) {
        log.debug("Удаление локального адреса {}", id);
    }

    @Override
    public AddressDTO getAddress(int id) {
        AddressDTO dto = new AddressDTO();
        dto.setId(id);
        dto.setCreatedAt(OffsetDateTime.now());
        return dto;
    }

    @Override
    public List<OrderInfoDTO> listOrders() {
        return Collections.emptyList();
    }

    @Override
    public OrderInfoDTO getOrder(String id) {
        return OrderInfoDTO.builder()
                .uuid(id)
                .provider(getProvider())
                .currentStatus("LOCAL")
                .build();
    }

    @Override
    public OrderInfoDTO createOrder(CreateOrderRequest req) {
        return OrderInfoDTO.builder()
                .orderId(counter.incrementAndGet())
                .trackingCode("LOCAL-" + System.currentTimeMillis())
                .currentStatus("CREATE")
                .provider(getProvider())
                .build();
    }

    @Override
    public OrderInfoDTO updateOrder(int orderId, CreateOrderRequest req) {
        return OrderInfoDTO.builder()
                .orderId(orderId)
                .currentStatus("UPDATED")
                .provider(getProvider())
                .build();
    }

    @Override
    public void deleteOrder(int orderId) {
        log.debug("Удаление локального заказа {}", orderId);
    }
}
