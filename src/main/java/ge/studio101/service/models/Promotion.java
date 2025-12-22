package ge.studio101.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotion")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "scope", length = 200)
    private String scope;

    @Column(name = "discount", length = 50)
    private String discount;

    @Column(name = "period", length = 120)
    private String period;

    @Column(name = "status", length = 50)
    private String status;
}
