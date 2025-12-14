package ge.studio101.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long itemId;
    private String name;
    private String colorName;
    private String sizeName;
    private Integer quantity;
    private BigDecimal price;
    private Long photoId;
}
