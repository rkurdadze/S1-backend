package ge.studio101.service.dto;

import lombok.Data;

@Data
public class PhotoNewDTO {
    private Long itemId;
    private String colorName;
    private byte[] image;
}
