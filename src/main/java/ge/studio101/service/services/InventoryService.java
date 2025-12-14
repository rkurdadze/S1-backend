package ge.studio101.service.services;

import ge.studio101.service.dto.ColorDTO;
import ge.studio101.service.dto.InventoryDTO;
import ge.studio101.service.dto.InventoryNewDTO;
import ge.studio101.service.dto.InventoryUpdateDTO;
import ge.studio101.service.mappers.InventoryMapper;
import ge.studio101.service.models.Inventory;
import ge.studio101.service.models.Item;
import ge.studio101.service.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    @Autowired
    private final InventoryRepository inventoryRepository;

    @Autowired
    private final InventoryMapper inventoryMapper;

    @Autowired
    private final ItemService itemService;





    public List<InventoryDTO> findAll() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList()); // ✅ Исправленное преобразование в список DTO
    }

    public Optional<InventoryDTO> findById(Long id) {
        return inventoryRepository.findById(id)
                .map(inventoryMapper::toDTO);
    }

    public List<InventoryDTO> findByItemId(Long itemId) {
        List<Long> colorIds = itemService.findById(itemId)
                .getColors() // Получаем List<ColorDTO>
                .stream() // Преобразуем в Stream<ColorDTO>
                .map(ColorDTO::getId) // Извлекаем id
                .collect(Collectors.toList()); // Собираем в List<Long>
        List<Inventory> inventories = inventoryRepository.findAllById_In(colorIds);
        return inventoryMapper.toDTOList(inventories);
    }

    public List<InventoryDTO> findByColorName(Long id, String colorName) {
        // Фильтруем только нужный цвет по имени и извлекаем его ID
        List<Long> colorIds = itemService.findById(id)
                .getColors() // Получаем List<ColorDTO>
                .stream()
                .filter(color -> color.getName().equals(colorName)) // ✅ Оставляем только нужный цвет
                .map(ColorDTO::getId) // Извлекаем id
                .collect(Collectors.toList()); // Собираем в List<Long>

        // Если цвет не найден, возвращаем пустой список
        if (colorIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Получаем инвентарь только для найденных цветов
        List<Inventory> inventories = inventoryRepository.findAllByColor_IdIn(colorIds);

        // Конвертируем в DTO и возвращаем
        return inventoryMapper.toDTOList(inventories);
    }

    public InventoryDTO save(InventoryNewDTO inventoryDTO) {
        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        Inventory savedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toDTO(savedInventory);
    }

    public InventoryDTO update(InventoryUpdateDTO inventoryUpdateDTO) {
        Optional<Inventory> inventory = inventoryRepository.findById(inventoryUpdateDTO.getId());
        if (inventory.isPresent()) {
            Inventory existingInventory = inventory.get();
            existingInventory.setStockCount(inventoryUpdateDTO.getStockCount());
            Inventory updatedInventory = inventoryRepository.save(existingInventory);
            return inventoryMapper.toDTO(updatedInventory);
        }
        return null;
    }

    public boolean deleteById(Long id) {
        if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
