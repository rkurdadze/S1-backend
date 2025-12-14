package ge.studio101.service.mappers;

import ge.studio101.service.dto.ItemDTO;
import ge.studio101.service.dto.ItemNewDTO;
import ge.studio101.service.models.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ColorMapper.class})
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "colors", source = "colors")
    ItemDTO toDTO(Item item);

    @Mapping(target = "id", ignore = true) // Игнорируем ID при создании нового элемента
    @Mapping(target = "colors", ignore = true) // Игнорируем цвета при создании
    Item toEntity(ItemNewDTO itemNewDTO);

    List<ItemDTO> toDTOList(List<Item> items);
}
