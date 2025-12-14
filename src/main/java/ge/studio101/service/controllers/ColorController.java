package ge.studio101.service.controllers;

import ge.studio101.service.dto.ColorDTO;
import ge.studio101.service.dto.ColorNewDTO;
import ge.studio101.service.dto.ItemDTO;
import ge.studio101.service.services.ColorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/colors")
@RequiredArgsConstructor
public class ColorController {
    private final ColorService colorService;

    @GetMapping
    public ResponseEntity<List<ColorDTO>> getAllColors() {
        return ResponseEntity.ok(colorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColorDTO> getColorById(@PathVariable Long id) {
        return colorService.findById(id)
                .map(ResponseEntity::ok) // ✅ Автоматически оборачиваем в `ResponseEntity.ok`
                .orElse(ResponseEntity.notFound().build()); // ✅ Если не найдено, возвращаем `404 Not Found`
    }

    @PostMapping
    public ResponseEntity<List<ColorDTO>> addColors(@RequestBody List<ColorNewDTO> colors) {
        List<ColorDTO> savedColors = colorService.saveAll(colors);
        return ResponseEntity.ok(savedColors);
    }

    @DeleteMapping
    public ResponseEntity<List<ColorDTO>> removeColor(@RequestBody ColorNewDTO color) {
        List<ColorDTO> presentColors = colorService.deleteColor(color);
        return ResponseEntity.ok(presentColors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColorDTO> updateColor(@PathVariable Long id, @RequestBody ColorNewDTO colorNewDTO) {
        ColorDTO updatedItem = colorService.updateColor(id, colorNewDTO);
        return updatedItem != null ? ResponseEntity.ok(updatedItem) : ResponseEntity.notFound().build();
    }
}
