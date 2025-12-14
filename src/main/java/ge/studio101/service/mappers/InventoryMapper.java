package ge.studio101.service.mappers;

import ge.studio101.service.dto.InventoryDTO;
import ge.studio101.service.dto.InventoryNewDTO;
import ge.studio101.service.models.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;


@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "size", source = "size")
    @Mapping(target = "colorId", source = "color.id")
    InventoryDTO toDTO(Inventory inventory);

    // Метод для преобразования InventoryDTO в Inventory
    @Mapping(target = "size", source = "size")
    @Mapping(target = "color.id", source = "colorId")
    Inventory toEntity(InventoryDTO inventoryDTO);

    @Mapping(target = "size.id", source = "sizeId")
    @Mapping(target = "color.id", source = "colorId")
    Inventory toEntity(InventoryNewDTO inventoryNewDTO);

    List<InventoryDTO> toDTOList(List<Inventory> inventories);
}
