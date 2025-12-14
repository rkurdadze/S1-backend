package ge.studio101.service.dto;

import lombok.Data;

@Data
public class InventoryNewDTO {
//    private Long id;
    private Integer stockCount;
    private Long sizeId;   // Полный объект SizeDTO
    private Long colorId;
}
