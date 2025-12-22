package ge.studio101.service.dto;

import lombok.Data;

import java.util.List;
import java.math.BigDecimal;

@Data
public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private boolean publish;
    private BigDecimal price;
    private List<ColorDTO> colors;
    private List<String> tags;
}
