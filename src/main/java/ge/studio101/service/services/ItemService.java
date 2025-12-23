package ge.studio101.service.services;

import ge.studio101.service.dto.ItemDTO;
import ge.studio101.service.dto.ItemNewDTO;
import ge.studio101.service.mappers.ItemMapper;
import ge.studio101.service.models.Item;
import ge.studio101.service.models.ItemTag;
import ge.studio101.service.models.ItemTagId;
import ge.studio101.service.models.Tag;
import ge.studio101.service.repositories.ItemRepository;
import ge.studio101.service.repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final ItemMapper itemMapper;

    @Autowired
    private final TagRepository tagRepository;

    public List<ItemDTO> findAll() {
        List<Item> items = itemRepository.findAll();
        return itemMapper.toDTOList(items);
    }

    public ItemDTO findById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toDTO)
                .orElse(null);
    }


    public ItemDTO save(ItemNewDTO itemNewDTO) {
        log.info("Saving new item: {}", itemNewDTO);
        Item item = itemMapper.toEntity(itemNewDTO);
        if (item.getPrice() == null) {
            item.setPrice(java.math.BigDecimal.ZERO);
        }
        applyTags(item, itemNewDTO.getTags());
        Item savedItem = itemRepository.save(item);
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
                .orElseGet(() -> tagRepository.save(new Tag(null, name, new java.util.HashSet<>())));
    }

    private ItemTag createItemTag(Item item, Tag tag) {
        return new ItemTag(new ItemTagId(item.getId(), tag.getId()), item, tag);
    }
}
