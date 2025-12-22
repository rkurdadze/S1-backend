package ge.studio101.service.controllers;

import ge.studio101.service.dto.PhotoAdminDTO;
import ge.studio101.service.dto.PhotoDTO;
import ge.studio101.service.dto.PhotoNewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ge.studio101.service.services.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;


import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Photos", description = "Операции с фото")
public class PhotoController {
    private final PhotoService photoService;

    @GetMapping("/photos")
    @Operation(summary = "Получить список фото")
    @ApiResponse(responseCode = "200", description = "Список фото")
    public ResponseEntity<List<PhotoDTO>> getAllPhotos() {
        return ResponseEntity.ok(photoService.findAll());
    }

    /**
     * Возвращает «сырые» байты изображения c корректным Content-Type,
     * определённым на основе первых байт (signature).
     */
    @GetMapping({"/photos/{id}", "/photos/{id}/{resolution}"})
    @Operation(summary = "Получить бинарные данные фото")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Бинарное изображение"),
            @ApiResponse(responseCode = "404", description = "Фото не найдено")
    })
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

    @PostMapping("/photos")
    @Operation(summary = "Добавить фото")
    @ApiResponse(responseCode = "200", description = "Фото добавлены")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','MANAGER')")
    public ResponseEntity<List<PhotoDTO>> addPhotos(@RequestBody List<PhotoNewDTO> photoNewDTOList) {
        List<PhotoDTO> savedPhotos = photoService.saveAll(photoNewDTOList);
        return ResponseEntity.ok(savedPhotos);
    }

    @DeleteMapping("/photos/{id}")
    @Operation(summary = "Удалить фото")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Фото удалено"),
            @ApiResponse(responseCode = "404", description = "Фото не найдено")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','MANAGER')")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id) {
        boolean deleted = photoService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/items/{itemId}/colors/{colorId}/photos")
    @Operation(summary = "Получить фото по товару и цвету")
    @ApiResponse(responseCode = "200", description = "Список фото по цвету")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR','MANAGER')")
    public ResponseEntity<List<PhotoAdminDTO>> getPhotosByItemAndColor(
            @PathVariable Long itemId,
            @PathVariable Long colorId) {
        return ResponseEntity.ok(photoService.findPhotosByItemAndColor(itemId, colorId));
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
