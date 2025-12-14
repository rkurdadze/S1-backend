package ge.studio101.service.controllers;

import ge.studio101.service.dto.*;
import ge.studio101.service.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getInventoryById(@PathVariable Long id) {
        Optional<InventoryDTO> inventoryDTO = inventoryService.findById(id);
        return inventoryDTO.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<List<InventoryDTO>> getInventoryByItemId(@PathVariable Long id) {
        List<InventoryDTO> inventoryDTO = inventoryService.findByItemId(id);
        return ResponseEntity.ok(inventoryDTO);
    }

    @GetMapping("/item/color_name/{id}/{colorName}")
    public ResponseEntity<List<InventoryDTO>> getInventoryByColorName(
            @PathVariable Long id,
            @PathVariable String colorName
    ) {
        List<InventoryDTO> inventoryDTO = inventoryService.findByColorName(id, colorName);
        return ResponseEntity.ok(inventoryDTO);
    }

    @PostMapping
    public ResponseEntity<InventoryDTO> addNew(@RequestBody InventoryNewDTO inventoryNewDTO) {
        try {
            InventoryDTO savedInventory = inventoryService.save(inventoryNewDTO);
            return ResponseEntity.ok(savedInventory);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping()
    public ResponseEntity<InventoryDTO> update(@RequestBody InventoryUpdateDTO inventoryUpdateDTO) {
        InventoryDTO updatedItem = inventoryService.update(inventoryUpdateDTO);
        return updatedItem != null ? ResponseEntity.ok(updatedItem) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        boolean deleted = inventoryService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


}

