package ge.studio101.service.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Indexed;

@Entity
@Table(name = "user_role", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Indexed
@Slf4j
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;
}

