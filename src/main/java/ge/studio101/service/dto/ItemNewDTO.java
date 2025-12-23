package ge.studio101.service.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.math.BigDecimal;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemNewDTO {
    private String name;
    private String description;
    private boolean publish;
    private BigDecimal price;
    private List<String> tags;
}
