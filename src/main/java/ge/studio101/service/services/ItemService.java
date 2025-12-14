package ge.studio101.service.services;

import ge.studio101.service.dto.ItemDTO;
import ge.studio101.service.dto.ItemNewDTO;
import ge.studio101.service.mappers.ItemMapper;
import ge.studio101.service.models.Item;
import ge.studio101.service.repositories.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
            existingItem.setPrice(itemNewDTO.getPrice());
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
}

