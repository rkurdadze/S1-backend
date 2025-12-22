package ge.studio101.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_zone")
public class DeliveryZone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "zone", nullable = false, length = 200)
    private String zone;

    @Column(name = "price", length = 50)
    private String price;

    @Column(name = "eta", length = 120)
    private String eta;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;
}
