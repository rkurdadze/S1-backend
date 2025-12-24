package ge.studio101.service.services;

import ge.studio101.service.dto.ItemDTO;
import ge.studio101.service.dto.ItemNewDTO;
import ge.studio101.service.mappers.ItemMapper;
import ge.studio101.service.models.Item;
import ge.studio101.service.models.ItemTag;
import ge.studio101.service.models.ItemTagId;
import ge.studio101.service.models.Tag;
import ge.studio101.service.repositories.ItemRepository;
import ge.studio101.service.repositories.PhotoRepository;
import ge.studio101.service.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@EnableAsync(mode = AdviceMode.ASPECTJ)
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final PhotoRepository photoRepository;
    private final ItemMapper itemMapper;
    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<ItemDTO> findAll() {
        List<Item> items = itemRepository.findAll().stream()
                .distinct()
                .toList();
        List<ItemDTO> itemDTOs = itemMapper.toDTOList(items);
        populatePhotoIds(itemDTOs);
        return itemDTOs;
    }

    @Transactional(readOnly = true)
    public ItemDTO findById(Long id) {
        ItemDTO itemDTO = itemRepository.findById(id)
                .map(itemMapper::toDTO)
                .orElse(null);
        
        if (itemDTO != null) {
            populatePhotoIds(List.of(itemDTO));
        }
        return itemDTO;
    }

    private void populatePhotoIds(List<ItemDTO> itemDTOs) {
        List<Long> colorIds = itemDTOs.stream()
                .filter(dto -> dto.getColors() != null)
                .flatMap(dto -> dto.getColors().stream())
                .map(ge.studio101.service.dto.ColorDTO::getId)
                .distinct()
                .toList();

        if (colorIds.isEmpty()) return;

        var photoProjections = photoRepository.findIdsByColorIdIn(colorIds);
        var photoIdsByColor = photoProjections.stream()
                .collect(Collectors.groupingBy(
                        ge.studio101.service.dto.PhotoIdProjection::getColorId,
                        Collectors.mapping(ge.studio101.service.dto.PhotoIdProjection::getId, Collectors.toList())
                ));

        itemDTOs.forEach(itemDTO -> {
            if (itemDTO.getColors() != null) {
                itemDTO.getColors().forEach(colorDTO -> {
                    colorDTO.setPhotoIds(photoIdsByColor.getOrDefault(colorDTO.getId(), List.of()));
                });
            }
        });
    }


    public ItemDTO save(ItemNewDTO itemNewDTO) {
        log.info("Saving new item: {}", itemNewDTO);
        Item item = itemMapper.toEntity(itemNewDTO);
        if (item.getPrice() == null) {
            item.setPrice(java.math.BigDecimal.ZERO);
        }
        
        // 1. Save item first to get an ID
        Item savedItem = itemRepository.save(item);
        
        // 2. Apply tags (now item has an ID)
        applyTags(savedItem, itemNewDTO.getTags());
        
        // 3. Save again with tags
        savedItem = itemRepository.save(savedItem);
        
        return itemMapper.toDTO(savedItem);
    }

    public ItemDTO update(Long id, ItemNewDTO itemNewDTO) {
        Optional<Item> existingItemOpt = itemRepository.findById(id);

        if (existingItemOpt.isPresent()) {
            Item existingItem = existingItemOpt.get();

            // Обновляем данные
            existingItem.setName(itemNewDTO.getName());
            existingItem.setDescription(itemNewDTO.getDescription());
            existingItem.setPublish(itemNewDTO.isPublish());
            existingItem.setPrice(itemNewDTO.getPrice() != null ? itemNewDTO.getPrice() : java.math.BigDecimal.ZERO);
            applyTags(existingItem, itemNewDTO.getTags());
            // Сохраняем обновленный элемент
            Item updatedItem = itemRepository.save(existingItem);

            // Возвращаем обновленный объект в формате DTO
            return itemMapper.toDTO(updatedItem);
        }

        return null; // Если элемент не найден
    }

    public boolean deleteById(Long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            itemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void applyTags(Item item, List<String> tags) {
        Set<ItemTag> resolvedTags = resolveTags(item, tags);
        if (item.getItemTags() == null) {
            item.setItemTags(new HashSet<>());
        }
        item.getItemTags().clear();
        item.getItemTags().addAll(resolvedTags);
    }

    private Set<ItemTag> resolveTags(Item item, List<String> tags) {
        if (tags == null) {
            return Set.of();
        }
        return tags.stream()
                .filter(tag -> tag != null && !tag.isBlank())
                .map(String::trim)
                .distinct()
                .map(this::resolveTag)
                .map(tag -> createItemTag(item, tag))
                .collect(Collectors.toSet());
    }

    private Tag resolveTag(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> tagRepository.save(new Tag(null, name, new HashSet<>(), new HashSet<>())));
    }

    private ItemTag createItemTag(Item item, Tag tag) {
        return new ItemTag(new ItemTagId(item.getId(), tag.getId()), item, tag);
    }
}