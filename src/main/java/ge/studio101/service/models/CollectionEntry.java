package ge.studio101.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "collection")
public class CollectionEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "tag", length = 100)
    private String tag;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "image", columnDefinition = "text")
    private String image;

    @Column(name = "anchor", length = 120)
    private String anchor;
}
