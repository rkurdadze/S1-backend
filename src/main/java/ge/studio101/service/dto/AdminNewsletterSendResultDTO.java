package ge.studio101.service.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class AdminNewsletterSendResultDTO {
    private Long id;
    private String subject;
    private OffsetDateTime sentAt;
    private String recipients;
}
