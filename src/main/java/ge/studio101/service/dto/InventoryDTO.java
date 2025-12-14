package ge.studio101.service.dto;

import lombok.Data;

@Data
public class InventoryDTO {
    private Long id;
    private Integer stockCount;
    private SizeDTO size;   // Полный объект SizeDTO
    private Long colorId;
}
