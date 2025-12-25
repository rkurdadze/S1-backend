package ge.studio101.service.dto.delivery;

import lombok.Data;

@Data
public class RegionDTO {
    private Integer id;
    private String nameEn;
    private String nameKa;
    private String nameRu;
    private Double latitude;
    private Double longitude;
}
