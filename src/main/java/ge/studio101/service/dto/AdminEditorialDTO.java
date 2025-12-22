package ge.studio101.service.dto;

import lombok.Data;

@Data
public class AdminEditorialDTO {
    private Long id;
    private String title;
    private String summary;
    private String image;
    private String cta;
}
