package ge.studio101.service.services;

import ge.studio101.service.dto.*;
import ge.studio101.service.models.*;
import ge.studio101.service.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminContentService {
    private static final Locale LOCALE_RU = Locale.forLanguageTag("ru");
    private static final DateTimeFormatter ORDER_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", LOCALE_RU);

    private final TagRepository tagRepository;
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final NewsRepository newsRepository;
    private final CollectionRepository collectionRepository;
    private final EditorialRepository editorialRepository;
    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrderRepository orderRepository;
    private final NewsletterDraftRepository newsletterDraftRepository;
    private final NewsletterSegmentRepository newsletterSegmentRepository;
    private final NewsletterSendRepository newsletterSendRepository;
    private final DeliveryZoneRepository deliveryZoneRepository;

    public List<String> getTags() {
        return tagRepository.findAll().stream()
                .map(Tag::getName)
                .sorted()
                .toList();
    }

    public void addTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return;
        }
        String trimmed = tag.trim();
        tagRepository.findByNameIgnoreCase(trimmed)
                .orElseGet(() -> tagRepository.save(new Tag(null, trimmed, new HashSet<>(), new HashSet<>())));
    }

    @Transactional
    public boolean deleteTag(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        Optional<Tag> tag = tagRepository.findByNameIgnoreCase(name.trim());
        if (tag.isEmpty()) {
            return false;
        }
        Tag stored = tag.get();
        itemRepository.findAll()
                .forEach(item -> item.getItemTags().removeIf(itemTag -> stored.equals(itemTag.getTag())));
        tagRepository.delete(stored);
        return true;
    }

    @Transactional
    public List<String> getItemTags(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Товар не найден: " + itemId));
        return item.getItemTags().stream()
                .map(ItemTag::getTag)
                .filter(Objects::nonNull)
                .map(Tag::getName)
                .sorted()
                .toList();
    }

    @Transactional
    public List<String> updateItemTags(Long itemId, List<String> updatedTags) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Товар не найден: " + itemId));
        Set<Tag> tags = updatedTags == null
                ? Set.of()
                : updatedTags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(tag -> !tag.isBlank())
                .distinct()
                .map(this::resolveTag)
                .collect(Collectors.toSet());
        item.getItemTags().clear();
        tags.forEach(tag -> item.getItemTags().add(new ItemTag(new ItemTagId(item.getId(), tag.getId()), item, tag)));
        itemRepository.save(item);
        return tags.stream()
                .map(Tag::getName)
                .sorted()
                .toList();
    }

    private Tag resolveTag(String name) {
        return tagRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> tagRepository.save(new Tag(null, name, new HashSet<>(), new HashSet<>())));
    }

    public List<AdminCategoryDTO> getCategories() {
        return categoryRepository.findAll().stream()
                .sorted(Comparator.comparing(Category::getId))
                .map(this::toCategoryDto)
                .toList();
    }

    @Transactional
    public AdminCategoryDTO createCategory(AdminCategoryDTO dto) {
        Category entity = new Category();
        entity.setTitle(dto.getTitle());
        // Save first to generate ID (if needed by JPA, though for ManyToMany often not strictly required if cascade works right, but safer)
        entity = categoryRepository.save(entity);
        applyCategory(dto, entity);
        return toCategoryDto(categoryRepository.save(entity));
    }

    @Transactional
    public AdminCategoryDTO updateCategory(Long id, AdminCategoryDTO dto) {
        Category entity = categoryRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        applyCategory(dto, entity);
        return toCategoryDto(categoryRepository.save(entity));
    }

    public boolean deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            return false;
        }
        categoryRepository.deleteById(id);
        return true;
    }

    private void applyCategory(AdminCategoryDTO source, Category target) {
        if (source == null) {
            return;
        }
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());
        target.setHighlight(source.getHighlight());
        target.setItemsCount(source.getItems() != null ? source.getItems() : 0);

        if (source.getTags() != null) {
            Set<Tag> tags = source.getTags().stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(tag -> !tag.isBlank())
                    .distinct()
                    .map(this::resolveTag)
                    .collect(Collectors.toSet());
            
            target.setTags(tags);
        }
    }

    private AdminCategoryDTO toCategoryDto(Category entity) {
        AdminCategoryDTO dto = new AdminCategoryDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setHighlight(entity.getHighlight());
        dto.setItems(entity.getItemsCount());
        dto.setTags(entity.getTags().stream()
                .map(Tag::getName)
                .sorted()
                .toList());
        return dto;
    }

    public List<AdminNewsDTO> getNews() {
        return newsRepository.findAll().stream()
                .sorted(Comparator.comparing(News::getId))
                .map(this::toNewsDto)
                .toList();
    }

    @Transactional
    public AdminNewsDTO createNews(AdminNewsDTO dto) {
        News entity = new News();
        applyNews(dto, entity);
        return toNewsDto(newsRepository.save(entity));
    }

    @Transactional
    public AdminNewsDTO updateNews(Long id, AdminNewsDTO dto) {
        News entity = newsRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        applyNews(dto, entity);
        return toNewsDto(newsRepository.save(entity));
    }

    public boolean deleteNews(Long id) {
        if (!newsRepository.existsById(id)) {
            return false;
        }
        newsRepository.deleteById(id);
        return true;
    }

    private void applyNews(AdminNewsDTO source, News target) {
        if (source == null) {
            return;
        }
        target.setTitle(source.getTitle());
        target.setDate(source.getDate());
        target.setSummary(source.getSummary());
        target.setImage(source.getImage());
    }

    private AdminNewsDTO toNewsDto(News entity) {
        AdminNewsDTO dto = new AdminNewsDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDate(entity.getDate());
        dto.setSummary(entity.getSummary());
        dto.setImage(entity.getImage());
        return dto;
    }

    public List<AdminCollectionDTO> getCollections() {
        return collectionRepository.findAll().stream()
                .sorted(Comparator.comparing(CollectionEntry::getId))
                .map(this::toCollectionDto)
                .toList();
    }

    @Transactional
    public AdminCollectionDTO createCollection(AdminCollectionDTO dto) {
        CollectionEntry entity = new CollectionEntry();
        applyCollection(dto, entity);
        return toCollectionDto(collectionRepository.save(entity));
    }

    @Transactional
    public AdminCollectionDTO updateCollection(Long id, AdminCollectionDTO dto) {
        CollectionEntry entity = collectionRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        applyCollection(dto, entity);
        return toCollectionDto(collectionRepository.save(entity));
    }

    public boolean deleteCollection(Long id) {
        if (!collectionRepository.existsById(id)) {
            return false;
        }
        collectionRepository.deleteById(id);
        return true;
    }

    private void applyCollection(AdminCollectionDTO source, CollectionEntry target) {
        if (source == null) {
            return;
        }
        target.setTitle(source.getTitle());
        target.setTag(source.getTag());
        target.setDescription(source.getDescription());
        target.setImage(source.getImage());
        target.setAnchor(source.getAnchor());
    }

    private AdminCollectionDTO toCollectionDto(CollectionEntry entity) {
        AdminCollectionDTO dto = new AdminCollectionDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setTag(entity.getTag());
        dto.setDescription(entity.getDescription());
        dto.setImage(entity.getImage());
        dto.setAnchor(entity.getAnchor());
        return dto;
    }

    public List<AdminEditorialDTO> getEditorials() {
        return editorialRepository.findAll().stream()
                .sorted(Comparator.comparing(Editorial::getId))
                .map(this::toEditorialDto)
                .toList();
    }

    @Transactional
    public AdminEditorialDTO createEditorial(AdminEditorialDTO dto) {
        Editorial entity = new Editorial();
        applyEditorial(dto, entity);
        return toEditorialDto(editorialRepository.save(entity));
    }

    @Transactional
    public AdminEditorialDTO updateEditorial(Long id, AdminEditorialDTO dto) {
        Editorial entity = editorialRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        applyEditorial(dto, entity);
        return toEditorialDto(editorialRepository.save(entity));
    }

    public boolean deleteEditorial(Long id) {
        if (!editorialRepository.existsById(id)) {
            return false;
        }
        editorialRepository.deleteById(id);
        return true;
    }

    private void applyEditorial(AdminEditorialDTO source, Editorial target) {
        if (source == null) {
            return;
        }
        target.setTitle(source.getTitle());
        target.setSummary(source.getSummary());
        target.setImage(source.getImage());
        target.setCta(source.getCta());
    }

    private AdminEditorialDTO toEditorialDto(Editorial entity) {
        AdminEditorialDTO dto = new AdminEditorialDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setSummary(entity.getSummary());
        dto.setImage(entity.getImage());
        dto.setCta(entity.getCta());
        return dto;
    }

    public List<AdminPromotionDTO> getPromotions() {
        return promotionRepository.findAll().stream()
                .sorted(Comparator.comparing(Promotion::getId))
                .map(this::toPromotionDto)
                .toList();
    }

    @Transactional
    public AdminPromotionDTO createPromotion(AdminPromotionDTO dto) {
        Promotion entity = new Promotion();
        applyPromotion(dto, entity);
        return toPromotionDto(promotionRepository.save(entity));
    }

    @Transactional
    public AdminPromotionDTO updatePromotion(Long id, AdminPromotionDTO dto) {
        Promotion entity = promotionRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        applyPromotion(dto, entity);
        return toPromotionDto(promotionRepository.save(entity));
    }

    public boolean deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            return false;
        }
        promotionRepository.deleteById(id);
        return true;
    }

    private void applyPromotion(AdminPromotionDTO source, Promotion target) {
        if (source == null) {
            return;
        }
        target.setName(source.getName());
        target.setScope(source.getScope());
        target.setDiscount(source.getDiscount());
        target.setPeriod(source.getPeriod());
        target.setStatus(source.getStatus());
    }

    private AdminPromotionDTO toPromotionDto(Promotion entity) {
        AdminPromotionDTO dto = new AdminPromotionDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setScope(entity.getScope());
        dto.setDiscount(entity.getDiscount());
        dto.setPeriod(entity.getPeriod());
        dto.setStatus(entity.getStatus());
        return dto;
    }

    public List<AdminUserDTO> getUsers() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(UserEntity::getId))
                .map(this::toUserDto)
                .toList();
    }

    @Transactional
    public AdminUserDTO createUser(AdminUserDTO dto) {
        UserEntity user = new UserEntity();
        applyUser(dto, user);
        if (user.getGoogleId() == null) {
            user.setGoogleId(user.getEmail());
        }
        if (user.getRole() == null) {
            roleRepository.findById(3L).ifPresent(user::setRole);
        }
        if (user.getStatus() == null) {
            user.setStatus("Активен");
        }
        if (user.getLastActive() == null) {
            user.setLastActive(OffsetDateTime.now());
        }
        return toUserDto(userRepository.save(user));
    }

    @Transactional
    public AdminUserDTO updateUser(Long id, AdminUserDTO dto) {
        UserEntity user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        applyUser(dto, user);
        return toUserDto(userRepository.save(user));
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    private void applyUser(AdminUserDTO source, UserEntity target) {
        if (source == null) {
            return;
        }
        if (source.getName() != null) {
            target.setName(source.getName());
        }
        if (source.getEmail() != null) {
            target.setEmail(source.getEmail());
            if (target.getGoogleId() == null) {
                target.setGoogleId(source.getEmail());
            }
        }
        if (source.getRole() != null) {
            roleRepository.findByNameIgnoreCase(source.getRole())
                    .ifPresent(target::setRole);
        }
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
        if (source.getLastActive() != null) {
            parseLastActive(source.getLastActive()).ifPresent(target::setLastActive);
        }
    }

    private Optional<OffsetDateTime> parseLastActive(String value) {
        try {
            return Optional.of(OffsetDateTime.parse(value));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private AdminUserDTO toUserDto(UserEntity user) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        dto.setStatus(user.getStatus());
        dto.setLastActive(formatLastActive(user.getLastActive()));
        return dto;
    }

    private String formatLastActive(OffsetDateTime value) {
        if (value == null) {
            return null;
        }
        return value.format(ORDER_DATE_FORMAT);
    }

    public List<AdminOrderDTO> getOrders() {
        return orderRepository.findAll().stream()
                .sorted(Comparator.comparing(CustomerOrder::getId))
                .map(this::toOrderDto)
                .toList();
    }

    @Transactional
    public AdminOrderDTO updateOrder(Long id, AdminOrderDTO dto) {
        CustomerOrder order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        applyOrder(dto, order);
        return toOrderDto(orderRepository.save(order));
    }

    public boolean deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            return false;
        }
        orderRepository.deleteById(id);
        return true;
    }

    private AdminOrderDTO toOrderDto(CustomerOrder order) {
        AdminOrderDTO dto = new AdminOrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber() != null ? order.getOrderNumber() : "#S1-" + order.getId());
        dto.setCustomer(order.getContact() != null ? order.getContact().getFullName() : null);
        dto.setTotal(order.getTotal() != null ? "₾ " + order.getTotal() : null);
        dto.setStatus(order.getStatus());
        dto.setDelivery(order.getDeliveryOption());
        dto.setDate(order.getCreatedAt() != null ? order.getCreatedAt().format(ORDER_DATE_FORMAT) : null);
        dto.setAddress(buildAddress(order.getContact()));
        dto.setNotes(order.getNotes());
        dto.setWindow(order.getDeliveryWindow());
        return dto;
    }

    private void applyOrder(AdminOrderDTO source, CustomerOrder target) {
        if (source == null) {
            return;
        }
        if (source.getOrderNumber() != null) {
            target.setOrderNumber(source.getOrderNumber());
        }
        if (source.getStatus() != null) {
            target.setStatus(source.getStatus());
        }
        if (source.getDelivery() != null) {
            target.setDeliveryOption(source.getDelivery());
        }
        if (source.getNotes() != null) {
            target.setNotes(source.getNotes());
        }
        if (source.getWindow() != null) {
            target.setDeliveryWindow(source.getWindow());
        }
        if (source.getAddress() != null) {
            CustomerContact contact = target.getContact();
            if (contact == null) {
                contact = new CustomerContact();
            }
            contact.setAddressLine(source.getAddress());
            target.setContact(contact);
        }
    }

    private String buildAddress(CustomerContact contact) {
        if (contact == null) {
            return null;
        }
        if (contact.getCity() != null && contact.getAddressLine() != null) {
            return contact.getAddressLine() + ", " + contact.getCity();
        }
        return contact.getAddressLine() != null ? contact.getAddressLine() : contact.getCity();
    }

    public AdminNewsletterDraftDTO getDraft() {
        NewsletterDraft draft = newsletterDraftRepository.findAll().stream().findFirst().orElseGet(() -> {
            NewsletterDraft created = new NewsletterDraft();
            created.setUpdatedAt(OffsetDateTime.now());
            return newsletterDraftRepository.save(created);
        });
        AdminNewsletterDraftDTO dto = new AdminNewsletterDraftDTO();
        dto.setSubject(draft.getSubject());
        dto.setMessage(draft.getMessage());
        return dto;
    }

    @Transactional
    public AdminNewsletterDraftDTO updateDraft(AdminNewsletterDraftDTO draft) {
        NewsletterDraft entity = newsletterDraftRepository.findAll().stream().findFirst().orElseGet(() -> {
            NewsletterDraft created = new NewsletterDraft();
            created.setUpdatedAt(OffsetDateTime.now());
            return created;
        });
        if (draft != null) {
            entity.setSubject(draft.getSubject());
            entity.setMessage(draft.getMessage());
            entity.setUpdatedAt(OffsetDateTime.now());
        }
        NewsletterDraft saved = newsletterDraftRepository.save(entity);
        AdminNewsletterDraftDTO dto = new AdminNewsletterDraftDTO();
        dto.setSubject(saved.getSubject());
        dto.setMessage(saved.getMessage());
        return dto;
    }

    public List<AdminNewsletterSegmentDTO> getSegments() {
        return newsletterSegmentRepository.findAll().stream()
                .sorted(Comparator.comparing(NewsletterSegment::getId))
                .map(this::toSegmentDto)
                .toList();
    }

    @Transactional
    public AdminNewsletterSegmentDTO createSegment(AdminNewsletterSegmentDTO dto) {
        NewsletterSegment entity = new NewsletterSegment();
        applySegment(dto, entity);
        return toSegmentDto(newsletterSegmentRepository.save(entity));
    }

    public boolean deleteSegment(Long id) {
        if (!newsletterSegmentRepository.existsById(id)) {
            return false;
        }
        newsletterSegmentRepository.deleteById(id);
        return true;
    }

    private void applySegment(AdminNewsletterSegmentDTO source, NewsletterSegment target) {
        if (source == null) {
            return;
        }
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setCount(source.getCount() != null ? source.getCount() : 0);
    }

    private AdminNewsletterSegmentDTO toSegmentDto(NewsletterSegment entity) {
        AdminNewsletterSegmentDTO dto = new AdminNewsletterSegmentDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCount(entity.getCount());
        return dto;
    }

    @Transactional
    public AdminNewsletterSendResultDTO sendNewsletter(AdminNewsletterSendDTO sendRequest) {
        NewsletterSend send = new NewsletterSend();
        send.setSubject(sendRequest != null ? sendRequest.getSubject() : null);
        send.setMessage(sendRequest != null ? sendRequest.getMessage() : null);
        send.setSentAt(OffsetDateTime.now());
        Set<NewsletterSegment> segments = resolveSegments(sendRequest);
        send.setSegments(segments);
        send.setRecipients(String.valueOf(segments.stream()
                .map(NewsletterSegment::getCount)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum()));
        NewsletterSend saved = newsletterSendRepository.save(send);
        AdminNewsletterSendResultDTO result = new AdminNewsletterSendResultDTO();
        result.setId(saved.getId());
        result.setSubject(saved.getSubject());
        result.setSentAt(saved.getSentAt());
        result.setRecipients(saved.getRecipients());
        if (sendRequest != null && Boolean.TRUE.equals(sendRequest.getTest())) {
            log.info("Отправка тестовой рассылки: {}", sendRequest.getSubject());
        }
        return result;
    }

    private Set<NewsletterSegment> resolveSegments(AdminNewsletterSendDTO sendRequest) {
        if (sendRequest == null || sendRequest.getSegmentIds() == null) {
            return Set.of();
        }
        return sendRequest.getSegmentIds().stream()
                .map(newsletterSegmentRepository::findById)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
    }

    public List<AdminDeliveryZoneDTO> getDeliveryZones() {
        return deliveryZoneRepository.findAll().stream()
                .sorted(Comparator.comparing(DeliveryZone::getId))
                .map(this::toDeliveryZoneDto)
                .toList();
    }

    @Transactional
    public AdminDeliveryZoneDTO createDeliveryZone(AdminDeliveryZoneDTO dto) {
        DeliveryZone entity = new DeliveryZone();
        applyDeliveryZone(dto, entity);
        return toDeliveryZoneDto(deliveryZoneRepository.save(entity));
    }

    @Transactional
    public AdminDeliveryZoneDTO updateDeliveryZone(Long id, AdminDeliveryZoneDTO dto) {
        DeliveryZone entity = deliveryZoneRepository.findById(id).orElse(null);
        if (entity == null) {
            return null;
        }
        applyDeliveryZone(dto, entity);
        return toDeliveryZoneDto(deliveryZoneRepository.save(entity));
    }

    public boolean deleteDeliveryZone(Long id) {
        if (!deliveryZoneRepository.existsById(id)) {
            return false;
        }
        deliveryZoneRepository.deleteById(id);
        return true;
    }

    private void applyDeliveryZone(AdminDeliveryZoneDTO source, DeliveryZone target) {
        if (source == null) {
            return;
        }
        target.setZone(source.getZone());
        target.setPrice(source.getPrice());
        target.setEta(source.getEta());
        target.setNotes(source.getNotes());
    }

    private AdminDeliveryZoneDTO toDeliveryZoneDto(DeliveryZone entity) {
        AdminDeliveryZoneDTO dto = new AdminDeliveryZoneDTO();
        dto.setId(entity.getId());
        dto.setZone(entity.getZone());
        dto.setPrice(entity.getPrice());
        dto.setEta(entity.getEta());
        dto.setNotes(entity.getNotes());
        return dto;
    }
}