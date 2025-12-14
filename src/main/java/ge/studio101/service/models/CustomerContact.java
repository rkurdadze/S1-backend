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

    @Column(name = "contact_name", length = 255)
    private String fullName;

    @Column(name = "contact_phone", length = 50)
    private String phone;

    @Column(name = "contact_email", length = 255)
    private String email;

    @Column(name = "contact_address", length = 500)
    private String addressLine;

    @Column(name = "contact_city", length = 120)
    private String city;

    @Column(name = "contact_postal", length = 40)
    private String postalCode;
}
