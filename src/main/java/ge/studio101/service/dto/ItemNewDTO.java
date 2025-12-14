package ge.studio101.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class ItemNewDTO {
    private String name;
    private String description;
    private boolean publish;
}
