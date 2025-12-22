package ge.studio101.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminNewsletterSendDTO {
    private String subject;
    private String message;
    private List<Long> segmentIds;
    private Boolean test;
}
