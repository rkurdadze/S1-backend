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

    @Mapping(target = "photoIds", source = "photos", qualifiedByName = "mapPhotoIds") // ✅ Исправлен source = "photos"
    @Mapping(target = "inventories", source = "inventories") // ✅ MapStruct автоматически мапит Inventory -> InventoryDTO
    ColorDTO toDTO(Color color);

    @Named("mapPhotoIds")
    default List<Long> mapPhotoIds(List<ge.studio101.service.models.Photo> photos) {
        return photos != null ? photos.stream().map(ge.studio101.service.models.Photo::getId).collect(Collectors.toList()) : null;
    }
}
