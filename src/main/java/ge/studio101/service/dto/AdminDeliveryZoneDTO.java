package ge.studio101.service.dto;

import lombok.Data;

@Data
public class AdminDeliveryZoneDTO {
    private Long id;
    private String zone;
    private String price;
    private String eta;
    private String notes;
}
