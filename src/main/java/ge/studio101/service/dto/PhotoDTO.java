package ge.studio101.service.dto;

import lombok.Data;

@Data
public class PhotoDTO {
    private Long id;
    private Long colorId; // ID цвета, без циклической зависимости
}
