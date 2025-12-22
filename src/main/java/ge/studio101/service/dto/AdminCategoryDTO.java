package ge.studio101.service.dto;

import lombok.Data;

@Data
public class AdminCategoryDTO {
    private Long id;
    private String title;
    private String description;
    private String highlight;
    private Integer items;
}
