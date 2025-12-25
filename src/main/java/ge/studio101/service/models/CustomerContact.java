package ge.studio101.service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class CustomerContact {

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "contact_name", length = 255)
    private String fullName;

    @Column(name = "contact_phone", length = 50)
    private String phone;

    @Column(name = "contact_email", length = 255)
    private String email;

    @Column(name = "contact_address", length = 500)
    private String addressLine;

    @Column(name = "address_line_1", length = 500)
    private String addressLine1;

    @Column(name = "address_line_2", length = 500)
    private String addressLine2;

    @Column(name = "contact_city", length = 120)
    private String city;

    @Column(name = "contact_postal", length = 40)
    private String postalCode;

    @Column(name = "municipality", length = 120)
    private String municipality;

    @Column(name = "region", length = 120)
    private String region;

    @Column(name = "country", length = 120)
    private String country;
}
