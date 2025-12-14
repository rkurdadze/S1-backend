package ge.studio101.service.controllers;

import ge.studio101.service.dto.PhotoDTO;
import ge.studio101.service.dto.PhotoNewDTO;
import ge.studio101.service.models.Photo;
import ge.studio101.service.services.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/photos")
@RequiredArgsConstructor
public class PhotoController {
    private final PhotoService photoService;

    @GetMapping
    public ResponseEntity<List<PhotoDTO>> getAllPhotos() {
        return ResponseEntity.ok(photoService.findAll());
    }

    /**
     * Возвращает «сырые» байты изображения c корректным Content-Type,
     * определённым на основе первых байт (signature).
     */
    @GetMapping({"/{id}", "/{id}/{resolution}"})
    public ResponseEntity<?> getPhotoBinary(
            @PathVariable Long id,
            @PathVariable(required = false) String resolution) throws IOException {
        try {
            byte[] bytes = photoService.getPhotoBinary(id, resolution);
            MediaType mediaType = determineMediaType(bytes);
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(bytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Фото с id " + id + " не найдено");
        }
    }

    @PostMapping
    public ResponseEntity<List<PhotoDTO>> addPhotos(@RequestBody List<PhotoNewDTO> photoNewDTOList) {
        List<PhotoDTO> savedPhotos = photoService.saveAll(photoNewDTOList);
        return ResponseEntity.ok(savedPhotos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        photoService.delete(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * Небольшой метод, определяющий MIME-тип по «магическим байтам».
     * Ниже приведены самые простые проверки для PNG и JPEG.
     * При необходимости можно расширить списком других форматов.
     */
    private MediaType determineMediaType(byte[] data) {
        if (data == null || data.length < 4) {
            // Если данных слишком мало, возвращаем octet-stream
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        // Проверяем PNG (начало: 89 50 4E 47)
        if (data[0] == (byte) 0x89 && data[1] == 0x50
                && data[2] == 0x4E && data[3] == 0x47) {
            return MediaType.IMAGE_PNG;
        }

        // Проверяем JPEG (начало: FF D8 FF)
        if (data[0] == (byte) 0xFF && data[1] == (byte) 0xD8
                && data[2] == (byte) 0xFF) {
            return MediaType.IMAGE_JPEG;
        }

        // Если не распознали, возвращаем octet-stream или сделайте дополнительные проверки
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
