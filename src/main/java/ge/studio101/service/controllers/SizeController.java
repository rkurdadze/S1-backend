package ge.studio101.service.controllers;

import ge.studio101.service.dto.SizeDTO;
import ge.studio101.service.services.SizeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sizes")
@RequiredArgsConstructor
public class SizeController {
    private final SizeService sizeService;

    @GetMapping
    public ResponseEntity<List<SizeDTO>> getAllSizes() {
        return ResponseEntity.ok(sizeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SizeDTO> getSizeById(@PathVariable Long id) {
        SizeDTO sizeDTO = sizeService.findById(id);
        return sizeDTO != null ? ResponseEntity.ok(sizeDTO) : ResponseEntity.notFound().build();
    }
}

