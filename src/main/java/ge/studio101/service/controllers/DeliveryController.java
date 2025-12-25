package ge.studio101.service.controllers;

import ge.studio101.service.delivery.DeliveryProvider;
import ge.studio101.service.delivery.DeliveryService;
import ge.studio101.service.delivery.DeliveryServiceFactory;
import ge.studio101.service.dto.AdminDeliverySettingsDTO;
import ge.studio101.service.dto.delivery.*;
import ge.studio101.service.services.AdminContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryServiceFactory deliveryServiceFactory;

    private final AdminContentService adminContentService;

    @GetMapping("/settings")
    @Operation(summary = "Получить настройки доставки")
    @ApiResponse(responseCode = "200", description = "Настройки доставки")
    public ResponseEntity<AdminDeliverySettingsDTO.Response> getDeliverySettings() {
        return ResponseEntity.ok(adminContentService.getDeliverySettings());
    }

    @GetMapping("/regions")
    public List<RegionDTO> getRegions(@RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).getRegions();
    }

    @GetMapping("/cities")
    public List<CityDTO> getCities(@RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).getCities();
    }

    @GetMapping("/regions/{regionId}/cities")
    public List<CityDTO> getCitiesByRegion(@PathVariable("regionId") int regionId,
                                           @RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).getCitiesByRegion(regionId);
    }

    @GetMapping("/pudo/cities")
    public List<PudoCityDTO> getPudoCities(@RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).getPudoCities();
    }

    @GetMapping("/city/{cityId}/pudos")
    public List<PudoDTO> getPudosByCity(@PathVariable("cityId") int cityId,
                                        @RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).getPudosByCity(cityId);
    }

    @GetMapping("/addresses")
    public List<AddressDTO> getAddresses(@RequestParam(value = "type", required = false) AddressType type,
                                         @RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).listAddresses(type);
    }

    @PostMapping("/addresses")
    public AddressDTO createAddress(@RequestBody CreateAddressRequest request,
                                    @RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).createAddress(request);
    }

    @GetMapping("/addresses/{id}")
    public AddressDTO getAddress(@PathVariable("id") int id,
                                 @RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).getAddress(id);
    }

    @PutMapping("/addresses/{id}")
    public AddressDTO updateAddress(@PathVariable("id") int id,
                                    @RequestBody UpdateAddressRequest request,
                                    @RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).updateAddress(id, request);
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") int id,
                                              @RequestParam(value = "provider", required = false) String provider) {
        resolve(provider).deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    public List<OrderInfoDTO> listOrders(@RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).listOrders();
    }

    @PostMapping("/orders")
    public OrderInfoDTO createOrder(@RequestBody CreateOrderRequest request,
                                    @RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).createOrder(request);
    }

    @GetMapping("/orders/{id}")
    public OrderInfoDTO getOrder(@PathVariable("id") String id,
                                 @RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).getOrder(id);
    }

    @PutMapping("/orders/{id}")
    public OrderInfoDTO updateOrder(@PathVariable("id") int id,
                                    @RequestBody CreateOrderRequest request,
                                    @RequestParam(value = "provider", required = false) String provider) {
        return resolve(provider).updateOrder(id, request);
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("id") int id,
                                            @RequestParam(value = "provider", required = false) String provider) {
        resolve(provider).deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    private DeliveryService resolve(String provider) {
        DeliveryProvider chosen = DeliveryProvider.TRACKINGS_GE;
        if (provider != null && !provider.isBlank()) {
            try {
                chosen = DeliveryProvider.valueOf(provider);
            } catch (IllegalArgumentException ignored) {
                chosen = DeliveryProvider.INTERNAL;
            }
        }
        DeliveryProvider finalChosen = chosen;
        return deliveryServiceFactory.getService(finalChosen)
                .orElseThrow(() -> new IllegalStateException("Служба доставки недоступна: " + finalChosen));
    }
}
