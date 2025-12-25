package ge.studio101.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_service_settings")
public class DeliveryServiceSetting {
    @Id
    @Column(name = "service", length = 50)
    private String service;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "label", nullable = false, length = 200)
    private String label;
}
