package ge.studio101.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "newsletter_send")
public class NewsletterSend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject", columnDefinition = "text")
    private String subject;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "sent_at", nullable = false)
    private OffsetDateTime sentAt;

    @Column(name = "recipients", length = 50)
    private String recipients;

    @ManyToMany
    @JoinTable(
            name = "newsletter_send_segment",
            joinColumns = @JoinColumn(name = "send_id"),
            inverseJoinColumns = @JoinColumn(name = "segment_id")
    )
    private Set<NewsletterSegment> segments = new HashSet<>();
}
