package ge.studio101.service.dto;

import lombok.Data;

@Data
public class AdminCollectionDTO {
    private Long id;
    private String title;
    private String tag;
    private String description;
    private String image;
    private String anchor;
}
