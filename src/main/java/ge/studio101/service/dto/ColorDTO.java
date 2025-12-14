package ge.studio101.service.dto;

import lombok.Data;
import java.util.List;

@Data
public class ColorDTO {
    private Long id;
    private String name;
    private List<Long> photoIds;
    private List<InventoryDTO> inventories; // ✅ Добавляем список инвентарей
}
