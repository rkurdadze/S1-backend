package ge.studio101.service.controllers;

import ge.studio101.service.dto.*;
import ge.studio101.service.services.AdminContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Административные эндпоинты управления контентом")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMINISTRATOR','MANAGER')")
public class AdminContentController {
    private final AdminContentService adminContentService;

    @GetMapping("/tags")
    @Operation(summary = "Получить список тегов")
    @ApiResponse(responseCode = "200", description = "Список тегов", content = @Content(schema = @Schema(implementation = String.class)))
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<String>> getTags() {
        return ResponseEntity.ok(adminContentService.getTags());
    }

    @PostMapping("/tags")
    @Operation(summary = "Добавить тег")
    @ApiResponse(responseCode = "200", description = "Тег добавлен")
    public ResponseEntity<Void> createTag(@RequestBody AdminTagCreateDTO request) {
        adminContentService.addTag(request != null ? request.getTag() : null);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tags/{name}")
    @Operation(summary = "Удалить тег")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Тег удалён"),
            @ApiResponse(responseCode = "404", description = "Тег не найден")
    })
    public ResponseEntity<Void> deleteTag(@PathVariable String name) {
        boolean deleted = adminContentService.deleteTag(name);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/items/{id}/tags")
    @Operation(summary = "Получить теги товара")
    @ApiResponse(responseCode = "200", description = "Список тегов товара")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<String>> getItemTags(@PathVariable Long id) {
        return ResponseEntity.ok(adminContentService.getItemTags(id));
    }

    @PutMapping("/items/{id}/tags")
    @Operation(summary = "Обновить теги товара")
    @ApiResponse(responseCode = "200", description = "Список тегов товара обновлён")
    public ResponseEntity<List<String>> updateItemTags(@PathVariable Long id, @RequestBody List<String> tags) {
        return ResponseEntity.ok(adminContentService.updateItemTags(id, tags));
    }

    @GetMapping("/categories")
    @Operation(summary = "Получить категории")
    @ApiResponse(responseCode = "200", description = "Список категорий")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdminCategoryDTO>> getCategories() {
        return ResponseEntity.ok(adminContentService.getCategories());
    }

    @PostMapping("/categories")
    @Operation(summary = "Создать категорию")
    @ApiResponse(responseCode = "200", description = "Категория создана")
    public ResponseEntity<AdminCategoryDTO> createCategory(@RequestBody AdminCategoryDTO request) {
        return ResponseEntity.ok(adminContentService.createCategory(request));
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "Обновить категорию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Категория обновлена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    public ResponseEntity<AdminCategoryDTO> updateCategory(@PathVariable Long id, @RequestBody AdminCategoryDTO request) {
        AdminCategoryDTO updated = adminContentService.updateCategory(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Удалить категорию")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Категория удалена"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        boolean deleted = adminContentService.deleteCategory(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/news")
    @Operation(summary = "Получить новости")
    @ApiResponse(responseCode = "200", description = "Список новостей")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdminNewsDTO>> getNews() {
        return ResponseEntity.ok(adminContentService.getNews());
    }

    @PostMapping("/news")
    @Operation(summary = "Создать новость")
    @ApiResponse(responseCode = "200", description = "Новость создана")
    public ResponseEntity<AdminNewsDTO> createNews(@RequestBody AdminNewsDTO request) {
        return ResponseEntity.ok(adminContentService.createNews(request));
    }

    @PutMapping("/news/{id}")
    @Operation(summary = "Обновить новость")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Новость обновлена"),
            @ApiResponse(responseCode = "404", description = "Новость не найдена")
    })
    public ResponseEntity<AdminNewsDTO> updateNews(@PathVariable Long id, @RequestBody AdminNewsDTO request) {
        AdminNewsDTO updated = adminContentService.updateNews(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/news/{id}")
    @Operation(summary = "Удалить новость")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Новость удалена"),
            @ApiResponse(responseCode = "404", description = "Новость не найдена")
    })
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        boolean deleted = adminContentService.deleteNews(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/collections")
    @Operation(summary = "Получить коллекции")
    @ApiResponse(responseCode = "200", description = "Список коллекций")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdminCollectionDTO>> getCollections() {
        return ResponseEntity.ok(adminContentService.getCollections());
    }

    @PostMapping("/collections")
    @Operation(summary = "Создать коллекцию")
    @ApiResponse(responseCode = "200", description = "Коллекция создана")
    public ResponseEntity<AdminCollectionDTO> createCollection(@RequestBody AdminCollectionDTO request) {
        return ResponseEntity.ok(adminContentService.createCollection(request));
    }

    @PutMapping("/collections/{id}")
    @Operation(summary = "Обновить коллекцию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Коллекция обновлена"),
            @ApiResponse(responseCode = "404", description = "Коллекция не найдена")
    })
    public ResponseEntity<AdminCollectionDTO> updateCollection(@PathVariable Long id, @RequestBody AdminCollectionDTO request) {
        AdminCollectionDTO updated = adminContentService.updateCollection(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/collections/{id}")
    @Operation(summary = "Удалить коллекцию")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Коллекция удалена"),
            @ApiResponse(responseCode = "404", description = "Коллекция не найдена")
    })
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        boolean deleted = adminContentService.deleteCollection(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/editorials")
    @Operation(summary = "Получить editorial")
    @ApiResponse(responseCode = "200", description = "Список editorial")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdminEditorialDTO>> getEditorials() {
        return ResponseEntity.ok(adminContentService.getEditorials());
    }

    @PostMapping("/editorials")
    @Operation(summary = "Создать editorial")
    @ApiResponse(responseCode = "200", description = "Editorial создан")
    public ResponseEntity<AdminEditorialDTO> createEditorial(@RequestBody AdminEditorialDTO request) {
        return ResponseEntity.ok(adminContentService.createEditorial(request));
    }

    @PutMapping("/editorials/{id}")
    @Operation(summary = "Обновить editorial")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Editorial обновлён"),
            @ApiResponse(responseCode = "404", description = "Editorial не найден")
    })
    public ResponseEntity<AdminEditorialDTO> updateEditorial(@PathVariable Long id, @RequestBody AdminEditorialDTO request) {
        AdminEditorialDTO updated = adminContentService.updateEditorial(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/editorials/{id}")
    @Operation(summary = "Удалить editorial")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Editorial удалён"),
            @ApiResponse(responseCode = "404", description = "Editorial не найден")
    })
    public ResponseEntity<Void> deleteEditorial(@PathVariable Long id) {
        boolean deleted = adminContentService.deleteEditorial(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/promotions")
    @Operation(summary = "Получить промоакции")
    @ApiResponse(responseCode = "200", description = "Список промоакций")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdminPromotionDTO>> getPromotions() {
        return ResponseEntity.ok(adminContentService.getPromotions());
    }

    @PostMapping("/promotions")
    @Operation(summary = "Создать промоакцию")
    @ApiResponse(responseCode = "200", description = "Промоакция создана")
    public ResponseEntity<AdminPromotionDTO> createPromotion(@RequestBody AdminPromotionDTO request) {
        return ResponseEntity.ok(adminContentService.createPromotion(request));
    }

    @PutMapping("/promotions/{id}")
    @Operation(summary = "Обновить промоакцию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Промоакция обновлена"),
            @ApiResponse(responseCode = "404", description = "Промоакция не найдена")
    })
    public ResponseEntity<AdminPromotionDTO> updatePromotion(@PathVariable Long id, @RequestBody AdminPromotionDTO request) {
        AdminPromotionDTO updated = adminContentService.updatePromotion(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/promotions/{id}")
    @Operation(summary = "Удалить промоакцию")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Промоакция удалена"),
            @ApiResponse(responseCode = "404", description = "Промоакция не найдена")
    })
    public ResponseEntity<Void> deletePromotion(@PathVariable Long id) {
        boolean deleted = adminContentService.deletePromotion(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/users")
    @Operation(summary = "Получить пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdminUserDTO>> getUsers() {
        return ResponseEntity.ok(adminContentService.getUsers());
    }

    @PostMapping("/users")
    @Operation(summary = "Создать пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь создан")
    public ResponseEntity<AdminUserDTO> createUser(@RequestBody AdminUserDTO request) {
        return ResponseEntity.ok(adminContentService.createUser(request));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Обновить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<AdminUserDTO> updateUser(@PathVariable Long id, @RequestBody AdminUserDTO request) {
        AdminUserDTO updated = adminContentService.updateUser(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Удалить пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = adminContentService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/orders")
    @Operation(summary = "Получить заказы")
    @ApiResponse(responseCode = "200", description = "Список заказов")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdminOrderDTO>> getOrders() {
        return ResponseEntity.ok(adminContentService.getOrders());
    }

    @PutMapping("/orders/{id}")
    @Operation(summary = "Обновить заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ обновлён"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<AdminOrderDTO> updateOrder(@PathVariable Long id, @RequestBody AdminOrderDTO request) {
        AdminOrderDTO updated = adminContentService.updateOrder(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/orders/{id}")
    @Operation(summary = "Удалить заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заказ удалён"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        boolean deleted = adminContentService.deleteOrder(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/newsletter/draft")
    @Operation(summary = "Получить черновик рассылки")
    @ApiResponse(responseCode = "200", description = "Черновик рассылки")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AdminNewsletterDraftDTO> getNewsletterDraft() {
        return ResponseEntity.ok(adminContentService.getDraft());
    }

    @PutMapping("/newsletter/draft")
    @Operation(summary = "Обновить черновик рассылки")
    @ApiResponse(responseCode = "200", description = "Черновик рассылки обновлён")
    public ResponseEntity<AdminNewsletterDraftDTO> updateNewsletterDraft(@RequestBody AdminNewsletterDraftDTO request) {
        return ResponseEntity.ok(adminContentService.updateDraft(request));
    }

    @GetMapping("/newsletter/segments")
    @Operation(summary = "Получить сегменты рассылки")
    @ApiResponse(responseCode = "200", description = "Список сегментов рассылки")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdminNewsletterSegmentDTO>> getNewsletterSegments() {
        return ResponseEntity.ok(adminContentService.getSegments());
    }

    @PostMapping("/newsletter/segments")
    @Operation(summary = "Создать сегмент рассылки")
    @ApiResponse(responseCode = "200", description = "Сегмент рассылки создан")
    public ResponseEntity<AdminNewsletterSegmentDTO> createNewsletterSegment(@RequestBody AdminNewsletterSegmentDTO request) {
        return ResponseEntity.ok(adminContentService.createSegment(request));
    }

    @DeleteMapping("/newsletter/segments/{id}")
    @Operation(summary = "Удалить сегмент рассылки")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Сегмент удалён"),
            @ApiResponse(responseCode = "404", description = "Сегмент не найден")
    })
    public ResponseEntity<Void> deleteNewsletterSegment(@PathVariable Long id) {
        boolean deleted = adminContentService.deleteSegment(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/newsletter/send")
    @Operation(summary = "Отправить рассылку")
    @ApiResponse(responseCode = "200", description = "Рассылка отправлена")
    public ResponseEntity<AdminNewsletterSendResultDTO> sendNewsletter(@RequestBody AdminNewsletterSendDTO request) {
        return ResponseEntity.ok(adminContentService.sendNewsletter(request));
    }

    @GetMapping("/delivery/settings")
    @Operation(summary = "Получить настройки доставки")
    @ApiResponse(responseCode = "200", description = "Настройки доставки")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AdminDeliverySettingsDTO.Response> getDeliverySettings() {
        // log.info("GET /api/v1/admin/delivery/settings called"); // We don't have SLF4J here usually, but let's check imports.
        // AdminContentController has @RequiredArgsConstructor but maybe not @Slf4j.
        // Let's check imports first.
        return ResponseEntity.ok(adminContentService.getDeliverySettings());
    }

    @PutMapping("/delivery/settings")
    @Operation(summary = "Обновить настройки доставки")
    @ApiResponse(responseCode = "200", description = "Настройки обновлены")
    public ResponseEntity<AdminDeliverySettingsDTO.Response> updateDeliverySettings(@RequestBody AdminDeliverySettingsDTO.UpdateRequest request) {
        return ResponseEntity.ok(adminContentService.updateDeliverySettings(request));
    }

    @GetMapping("/delivery-zones")
    @Operation(summary = "Получить зоны доставки")
    @ApiResponse(responseCode = "200", description = "Список зон доставки")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<AdminDeliveryZoneDTO>> getDeliveryZones() {
        return ResponseEntity.ok(adminContentService.getDeliveryZones());
    }

    @PostMapping("/delivery-zones")
    @Operation(summary = "Создать зону доставки")
    @ApiResponse(responseCode = "200", description = "Зона доставки создана")
    public ResponseEntity<AdminDeliveryZoneDTO> createDeliveryZone(@RequestBody AdminDeliveryZoneDTO request) {
        return ResponseEntity.ok(adminContentService.createDeliveryZone(request));
    }

    @PutMapping("/delivery-zones/{id}")
    @Operation(summary = "Обновить зону доставки")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Зона доставки обновлена"),
            @ApiResponse(responseCode = "404", description = "Зона доставки не найдена")
    })
    public ResponseEntity<AdminDeliveryZoneDTO> updateDeliveryZone(@PathVariable Long id, @RequestBody AdminDeliveryZoneDTO request) {
        AdminDeliveryZoneDTO updated = adminContentService.updateDeliveryZone(id, request);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delivery-zones/{id}")
    @Operation(summary = "Удалить зону доставки")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Зона доставки удалена"),
            @ApiResponse(responseCode = "404", description = "Зона доставки не найдена")
    })
    public ResponseEntity<Void> deleteDeliveryZone(@PathVariable Long id) {
        boolean deleted = adminContentService.deleteDeliveryZone(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
