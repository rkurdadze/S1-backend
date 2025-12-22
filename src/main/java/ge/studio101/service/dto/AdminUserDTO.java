package ge.studio101.service.dto;

import lombok.Data;

@Data
public class AdminUserDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String status;
    private String lastActive;
}
