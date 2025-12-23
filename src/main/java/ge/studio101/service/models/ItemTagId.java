package ge.studio101.service.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ItemTagId implements Serializable {
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "tag_id")
    private Long tagId;
}
