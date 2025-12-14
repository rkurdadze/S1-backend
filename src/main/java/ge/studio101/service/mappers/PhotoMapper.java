package ge.studio101.service.mappers;

import ge.studio101.service.dto.PhotoDTO;
import ge.studio101.service.models.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PhotoMapper {
    PhotoMapper INSTANCE = Mappers.getMapper(PhotoMapper.class);

    @Mapping(target = "colorId", source = "color.id")
    PhotoDTO toDTO(Photo photo);

    List<PhotoDTO> toDTOList(List<Photo> photos);
}
