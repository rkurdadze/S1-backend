package ge.studio101.service.mappers;

import ge.studio101.service.dto.SizeDTO;
import ge.studio101.service.models.Size;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SizeMapper {
    SizeMapper INSTANCE = Mappers.getMapper(SizeMapper.class);

    SizeDTO toDTO(Size size);

    List<SizeDTO> toDTOList(List<Size> sizes);
}
