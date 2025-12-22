package ge.studio101.service.dto;

import lombok.Data;

@Data
public class AdminNewsDTO {
    private Long id;
    private String title;
    private String date;
    private String summary;
    private String image;
}
