package ge.studio101.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "newsletter_draft")
public class NewsletterDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject", columnDefinition = "text")
    private String subject;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
