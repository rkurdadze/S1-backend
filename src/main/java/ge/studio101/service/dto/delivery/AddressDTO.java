package ge.studio101.service.dto.delivery;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AddressDTO {
    private Integer id;
    private AddressType type;
    private String address;
    private Double latitude;
    private Double longitude;
    private OffsetDateTime createdAt;
    private String state;
    private CityDTO city;
}
