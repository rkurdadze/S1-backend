package ge.studio101.service.delivery;

import ge.studio101.service.dto.delivery.*;

import java.util.List;

public interface DeliveryService {
    DeliveryProvider getProvider();

    List<RegionDTO> getRegions();
    List<CityDTO> getCities();
    List<CityDTO> getCitiesByRegion(int regionId);
    List<PudoCityDTO> getPudoCities();
    List<PudoDTO> getPudosByCity(int cityId);

    List<AddressDTO> listAddresses(AddressType type);
    AddressDTO createAddress(CreateAddressRequest req);
    AddressDTO updateAddress(int id, UpdateAddressRequest req);
    void deleteAddress(int id);
    AddressDTO getAddress(int id);

    List<OrderInfoDTO> listOrders();
    OrderInfoDTO getOrder(String id);
    OrderInfoDTO createOrder(CreateOrderRequest req);
    OrderInfoDTO updateOrder(int orderId, CreateOrderRequest req);
    void deleteOrder(int orderId);
}
