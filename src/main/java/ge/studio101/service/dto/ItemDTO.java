package ge.studio101.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private boolean publish;
    private List<ColorDTO> colors;
}
