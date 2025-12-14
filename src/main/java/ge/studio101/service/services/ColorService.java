package ge.studio101.service.services;

import ge.studio101.service.dto.ColorDTO;
import ge.studio101.service.dto.ColorNewDTO;
import ge.studio101.service.mappers.ColorMapper;
import ge.studio101.service.models.Color;
import ge.studio101.service.repositories.ColorRepository;
import ge.studio101.service.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ColorService {

    private final ColorRepository colorRepository;
    private final ColorMapper colorMapper;
    private final ItemRepository itemRepository;

    public List<ColorDTO> findAll() {
        return colorRepository.findAll().stream()
                .map(colorMapper::toDTO) // ✅ Используем `toDTO` вместо `toDTOList`
                .collect(Collectors.toList());
    }

    public Optional<ColorDTO> findById(Long id) {
        return colorRepository.findById(id)
                .map(colorMapper::toDTO);
    }

    public List<ColorDTO> saveAll(List<ColorNewDTO> colors) {
        List<Color> colorsToSave = colors.stream()
                .filter(colorNewDTO -> !colorRepository.existsByNameAndItemId(colorNewDTO.getName(), colorNewDTO.getItem_id())) // Проверка существования
                .map(colorNewDTO -> {
                    Color color = new Color();
                    color.setName(colorNewDTO.getName());
                    color.setItem(itemRepository.findById(colorNewDTO.getItem_id()).orElse(null));
                    return color;
                })
                .collect(Collectors.toList());

        List<Color> savedColors = colorRepository.saveAll(colorsToSave);
        return savedColors.stream()
                .map(colorMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ColorDTO> deleteColor(ColorNewDTO color) {
        Color colorToRemove = colorRepository.findColorByItem_IdAndName(color.getItem_id(), color.getName());
        colorRepository.delete(colorToRemove);
        return colorRepository.findByItem_Id(color.getItem_id())
                .stream().map(colorMapper::toDTO).collect(Collectors.toList());
    }

    public ColorDTO updateColor(Long id, ColorNewDTO colorNewDTO) {
        Optional<Color> existingColor = colorRepository.findById(id);

        if (existingColor.isPresent()) {
            Color color = existingColor.get();
            color.setName(colorNewDTO.getName());
            color.setItem(itemRepository.findById(colorNewDTO.getItem_id()).orElse(null));
            Color updatedColor = colorRepository.save(color);
            return colorMapper.toDTO(updatedColor);
        }

        return null;
    }
}
