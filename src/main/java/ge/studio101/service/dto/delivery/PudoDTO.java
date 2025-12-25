package ge.studio101.service.dto.delivery;

import lombok.Data;

@Data
public class PudoDTO {
    private Integer id;
    private Integer cityId;
    private Integer pudoId;
    private String nameKa;
    private String cityName;
    private Double latitude;
    private Double longitude;
    private CityDTO city;
}
