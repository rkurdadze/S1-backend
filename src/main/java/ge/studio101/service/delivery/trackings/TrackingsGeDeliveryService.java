package ge.studio101.service.delivery.trackings;

import ge.studio101.service.delivery.DeliveryProvider;
import ge.studio101.service.delivery.DeliveryService;
import ge.studio101.service.delivery.exception.DeliveryClientException;
import ge.studio101.service.delivery.exception.TooManyRequestsException;
import ge.studio101.service.dto.delivery.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrackingsGeDeliveryService implements DeliveryService {

    private static final int MAX_RETRIES = 3;

    private final RestTemplate trackingsGeRestTemplate;
    private final TrackingsGeProperties properties;

    @Override
    public DeliveryProvider getProvider() {
        return DeliveryProvider.TRACKINGS_GE;
    }

    @Override
    public List<RegionDTO> getRegions() {
        TrackingsResponse<List<RegionDTO>> response = exchange("/api/geo/regions", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
    }

    @Override
    public List<CityDTO> getCities() {
        TrackingsResponse<List<CityDTO>> response = exchange("/api/geo/cities", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
    }

    @Override
    public List<CityDTO> getCitiesByRegion(int regionId) {
        TrackingsResponse<List<CityDTO>> response = exchange("/api/geo/region/" + regionId + "/cities", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
    }

    @Override
    public List<PudoCityDTO> getPudoCities() {
        TrackingsResponse<List<PudoCityDTO>> response = exchange("/api/geo/pudo/cities", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
    }

    @Override
    public List<PudoDTO> getPudosByCity(int cityId) {
        TrackingsResponse<List<PudoDTO>> response = exchange("/api/geo/city/" + cityId + "/pudos", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
    }

    @Override
    public List<AddressDTO> listAddresses(AddressType type) {
        String uri = "/api/addresses" + (type != null ? "?type=" + type.name() : "");
        TrackingsResponse<List<AddressDTO>> response = exchange(uri, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
    }

    @Override
    public AddressDTO createAddress(CreateAddressRequest req) {
        TrackingsResponse<AddressDTO> response = exchange("/api/addresses", HttpMethod.POST, req,
                new ParameterizedTypeReference<>() {
                });
        if (response == null) {
            return null;
        }
        return response.getData();
    }

    @Override
    public AddressDTO updateAddress(int id, UpdateAddressRequest req) {
        TrackingsResponse<AddressDTO> response = exchange("/api/addresses/" + id, HttpMethod.PUT, req,
                new ParameterizedTypeReference<>() {
                });
        if (response == null) {
            return null;
        }
        return response.getData();
    }

    @Override
    public void deleteAddress(int id) {
        exchange("/api/addresses/" + id, HttpMethod.DELETE, null, new ParameterizedTypeReference<Void>() {
        });
    }

    @Override
    public AddressDTO getAddress(int id) {
        TrackingsResponse<AddressDTO> response = exchange("/api/addresses/" + id, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        if (response == null) {
            return null;
        }
        return response.getData();
    }

    @Override
    public List<OrderInfoDTO> listOrders() {
        TrackingsResponse<List<OrderInfoDTO>> response = exchange("/api/orders", HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        return response != null && response.getData() != null ? response.getData() : Collections.emptyList();
    }

    @Override
    public OrderInfoDTO getOrder(String id) {
        TrackingsResponse<OrderInfoDTO> response = exchange("/api/orders/" + id, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {
                });
        if (response == null) {
            return null;
        }
        OrderInfoDTO dto = response.getData();
        if (dto != null) {
            dto.setProvider(getProvider());
        }
        return dto;
    }

    @Override
    public OrderInfoDTO createOrder(CreateOrderRequest req) {
        TrackingsOrderResponse response = exchange("/api/orders", HttpMethod.POST, req,
                new ParameterizedTypeReference<>() {
                });
        if (response == null) {
            return null;
        }
        return response.toOrderInfo();
    }

    @Override
    public OrderInfoDTO updateOrder(int orderId, CreateOrderRequest req) {
        TrackingsOrderResponse response = exchange("/api/orders/" + orderId, HttpMethod.PUT, req,
                new ParameterizedTypeReference<>() {
                });
        if (response == null) {
            return null;
        }
        return response.toOrderInfo();
    }

    @Override
    public void deleteOrder(int orderId) {
        exchange("/api/orders/" + orderId, HttpMethod.DELETE, null, new ParameterizedTypeReference<Void>() {
        });
    }

    private <T> T exchange(String path, HttpMethod method, Object body, ParameterizedTypeReference<T> type) {
        String url = properties.getApiUrl() + path;
        Duration backoff = Duration.ofSeconds(1);
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                ResponseEntity<T> response = trackingsGeRestTemplate.exchange(url, method, new HttpEntity<>(body), type);
                return response.getBody();
            } catch (HttpStatusCodeException ex) {
                HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
                if (status == HttpStatus.TOO_MANY_REQUESTS && attempt < MAX_RETRIES - 1) {
                    Duration retryAfter = resolveRetryAfter(ex.getResponseHeaders() != null ? ex.getResponseHeaders().getFirst("Retry-After") : null, backoff);
                    log.warn("Trackings.ge вернул 429, повтор через {}", retryAfter);
                    sleep(retryAfter);
                    backoff = backoff.multipliedBy(2);
                    continue;
                }
                throw new DeliveryClientException(status, ex.getResponseBodyAsString());
            }
        }
        throw new TooManyRequestsException(backoff, "Превышено количество попыток обращения к Trackings.ge");
    }

    private Duration resolveRetryAfter(String retryAfter, Duration fallback) {
        try {
            if (retryAfter != null) {
                long seconds = Long.parseLong(retryAfter);
                return Duration.ofSeconds(seconds);
            }
        } catch (NumberFormatException ignored) {
        }
        return fallback;
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
