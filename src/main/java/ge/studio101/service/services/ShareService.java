package ge.studio101.service.services;

import ge.studio101.service.dto.ShareItemRequest;
import ge.studio101.service.dto.ShareLinkResponse;
import ge.studio101.service.models.Item;
import ge.studio101.service.models.ShareLink;
import ge.studio101.service.repositories.ItemRepository;
import ge.studio101.service.repositories.ShareLinkRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShareService {

    public static final String SHARE_PATH = "/api/v1/share/item/";
    private static final Duration SHARE_TTL = Duration.ofDays(7);

    private final ItemRepository itemRepository;
    private final ShareLinkRepository shareLinkRepository;

    public ShareLinkResponse createShareLink(Long pathItemId, ShareItemRequest request) {
        SharePayload payload = buildPayload(pathItemId, request);

        String token = UUID.randomUUID().toString();
        ShareLink entity = mapToEntity(payload, token);
        shareLinkRepository.save(entity);

        String shareUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(SHARE_PATH)
                .path(token)
                .toUriString();

        return new ShareLinkResponse(shareUrl);
    }

    public ShareLink getPayload(String token) {
        Optional<ShareLink> optional = shareLinkRepository.findById(token);
        if (optional.isEmpty()) {
            log.warn("Запрошенный payload не найден для токена {}", token);
            return null;
        }

        ShareLink shareLink = optional.get();
        if (shareLink.getExpiresAt() != null && shareLink.getExpiresAt().isBefore(Instant.now())) {
            log.info("Ссылка шаринга {} истекла в {}", token, shareLink.getExpiresAt());
            shareLinkRepository.deleteById(token);
            return null;
        }

        if (shareLink.getImages() == null) {
            shareLink.setImages(Collections.emptyList());
        }

        shareLink.setClickCount(shareLink.getClickCount() + 1);
        shareLinkRepository.save(shareLink);

        return shareLink;
    }

    public String buildSharePage(String shareUrl, ShareLink payload) {
        String targetUrl = HtmlUtils.htmlEscape(payload.getUrl());
        String title = HtmlUtils.htmlEscape(Optional.ofNullable(payload.getItemName()).orElse("S1"));
        String description = HtmlUtils.htmlEscape(Optional.ofNullable(payload.getDescription()).orElse(""));
        String caption = HtmlUtils.htmlEscape(Optional.ofNullable(payload.getCaption()).orElse(""));
        String ogDescription = description.isBlank() ? caption : description;
        String image = payload.getImages().isEmpty() ? null : HtmlUtils.htmlEscape(payload.getImages().get(0));
        String price = payload.getPrice() != null ? payload.getPrice().toPlainString() : "";

        StringBuilder html = new StringBuilder("""
                <!doctype html>
                <html lang="en">
                <head>
                <meta charset="UTF-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                """);

        html.append("""
                <title>%s</title>
                <meta property="og:type" content="website"/>
                <meta property="og:title" content="%s"/>
                <meta property="og:description" content="%s"/>
                <meta property="og:url" content="%s"/>
                """.formatted(title, title, ogDescription, HtmlUtils.htmlEscape(shareUrl)));

        if (StringUtils.hasText(image)) {
            html.append("""
                    <meta property="og:image" content="%s"/>
                    """.formatted(image));
        }

        if (StringUtils.hasText(price)) {
            html.append("""
                    <meta property="product:price:amount" content="%s"/>
                    """.formatted(HtmlUtils.htmlEscape(price)));
        }

        html.append("""
                </head>
                <body>
                <div style="display:none">
                """);

        if (StringUtils.hasText(payload.getColorName())) {
            html.append("<p>Color: ").append(HtmlUtils.htmlEscape(payload.getColorName())).append("</p>");
        }

        html.append("<p>").append(caption).append("</p>")
                .append("</div>")
                .append("""
                        <script>
                        (function(){
                          const ua = navigator.userAgent.toLowerCase();
                          const isSocialBot = ua.includes('facebookexternalhit') || ua.includes('instagram');
                          if(!isSocialBot){ window.location.replace('%s'); }
                        })();
                        </script>
                        <noscript><meta http-equiv="refresh" content="0;url=%s"></noscript>
                        </body>
                        </html>
                        """.formatted(targetUrl, targetUrl));

        return html.toString();
    }

    private ShareLink mapToEntity(SharePayload payload, String token) {
        ShareLink entity = new ShareLink();
        entity.setToken(token);
        entity.setPlatform(payload.getPlatform());
        entity.setDestination(payload.getDestination());
        entity.setCaption(payload.getCaption());
        entity.setUrl(payload.getUrl());
        entity.setImages(payload.getImages());
        entity.setColorName(payload.getColorName());
        entity.setItemName(payload.getItemName());
        entity.setDescription(payload.getDescription());
        entity.setPrice(payload.getPrice());
        entity.setItemId(payload.getItemId());
        Instant createdAt = Optional.ofNullable(payload.getCreatedAt()).orElse(Instant.now());
        entity.setCreatedAt(createdAt);
        entity.setExpiresAt(createdAt.plus(SHARE_TTL));
        return entity;
    }

    private SharePayload buildPayload(Long pathItemId, ShareItemRequest request) {
        if (!StringUtils.hasText(request.getUrl())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Поле url обязательно для шаринга");
        }

        Long itemId = request.getItemId() != null ? request.getItemId() : pathItemId;
        Item item = null;

        if (itemId != null) {
            item = itemRepository.findById(itemId).orElse(null);
        }

        String itemName = firstNonBlank(request.getItemName(), item != null ? item.getName() : null, "S1");
        String description = firstNonBlank(request.getDescription(), request.getCaption(), item != null ? item.getDescription() : null, "");
        BigDecimal price = request.getPrice() != null ? request.getPrice() : (item != null ? item.getPrice() : null);

        List<String> sanitizedImages = new ArrayList<>();
        if (request.getImages() != null) {
            for (String image : request.getImages()) {
                if (StringUtils.hasText(image)) {
                    sanitizedImages.add(image);
                }
            }
        }

        return new SharePayload(
                request.getPlatform(),
                request.getDestination(),
                request.getCaption(),
                request.getUrl(),
                Collections.unmodifiableList(sanitizedImages),
                request.getColorName(),
                itemName,
                description,
                price,
                itemId,
                Instant.now()
        );
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    public static class SharePayload {
        private String platform;
        private String destination;
        private String caption;
        private String url;
        private List<String> images;
        private String colorName;
        private String itemName;
        private String description;
        private BigDecimal price;
        private Long itemId;
        private Instant createdAt;
    }
}
