package ge.studio101.service.dto;

import lombok.Data;

@Data
public class ContactInfoDTO {
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private String email;
    private String addressLine;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;
    private String municipality;
    private String region;
    private String country;
}
