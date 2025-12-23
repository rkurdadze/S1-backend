package ge.studio101.service.mappers;

import ge.studio101.service.dto.ColorDTO;
import ge.studio101.service.dto.InventoryDTO;
import ge.studio101.service.models.Color;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {InventoryMapper.class})
public interface ColorMapper {
    ColorMapper INSTANCE = Mappers.getMapper(ColorMapper.class);

    @Mapping(target = "photoIds", ignore = true)
    @Mapping(target = "inventories", source = "inventories") 
    ColorDTO toDTO(Color color);
}
