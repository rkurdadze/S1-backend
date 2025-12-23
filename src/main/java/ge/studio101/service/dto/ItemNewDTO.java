package ge.studio101.service.dto;

import lombok.Data;

import java.util.List;
import java.math.BigDecimal;

@Data
public class ItemNewDTO {
    private String name;
    private String description;
    private boolean publish;
    private BigDecimal price;
    private List<String> tags;
}
