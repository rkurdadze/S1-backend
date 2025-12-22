package ge.studio101.service.dto;

import lombok.Data;

@Data
public class AdminPromotionDTO {
    private Long id;
    private String name;
    private String scope;
    private String discount;
    private String period;
    private String status;
}
