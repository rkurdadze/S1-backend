package ge.studio101.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShareItemRequest {
    private String platform;
    private String destination;
    private String caption;
    private String url;
    private List<String> images;
    private String colorName;
    private String itemName;
    private String description;
    private BigDecimal price;
    private Long itemId;
}
