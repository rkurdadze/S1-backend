package ge.studio101.service.controllers;

import ge.studio101.service.dto.ShareItemRequest;
import ge.studio101.service.dto.ShareLinkResponse;
import ge.studio101.service.services.ShareService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/share/item")
@RequiredArgsConstructor
@Slf4j
public class ShareController {

    private final ShareService shareService;

    @PostMapping("/{id}")
    public ResponseEntity<ShareLinkResponse> createShareLink(@PathVariable Long id, @RequestBody ShareItemRequest request) {
        ShareLinkResponse response = shareService.createShareLink(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{token}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> renderSharePage(@PathVariable String token) {
        var payload = shareService.getPayload(token);
        if (payload == null) {
            log.warn("Ссылка шаринга с токеном {} не найдена или истекла", token);
            return ResponseEntity.notFound().build();
        }

        String shareUrl = ServletUriComponentsBuilder.fromCurrentRequestUri().toUriString();
        String html = shareService.buildSharePage(shareUrl, payload);
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}
