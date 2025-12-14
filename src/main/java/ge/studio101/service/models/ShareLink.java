package ge.studio101.service.models;

import ge.studio101.service.helpers.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "share_link")
public class ShareLink {

    @Id
    @Column(length = 36, nullable = false)
    private String token;

    @Column(length = 50)
    private String platform;

    @Column(length = 50)
    private String destination;

    @Column(columnDefinition = "text")
    private String caption;

    @Column(columnDefinition = "text", nullable = false)
    private String url;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text")
    private List<String> images = new ArrayList<>();

    @Column(length = 100)
    private String colorName;

    @Column(length = 200)
    private String itemName;

    @Column(columnDefinition = "text")
    private String description;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    private Long itemId;

    private Instant createdAt;

    private Instant expiresAt;

    @Column(nullable = false)
    private long clickCount = 0L;
}
