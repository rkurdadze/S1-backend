package ge.studio101.service.dto;

import lombok.Data;

@Data
public class ContactInfoDTO {
    private String fullName;
    private String phone;
    private String email;
    private String addressLine;
    private String city;
    private String postalCode;
}
