package ge.studio101.service.repositories;

import ge.studio101.service.dto.ColorDTO;
import ge.studio101.service.models.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
    boolean existsByNameAndItemId(String name, Long itemId);

    Optional<Color> findByItemIdAndName(Long itemId, String name);

    Color findColorByItem_IdAndName(Long itemId, String name);

    List<Color> findByItem_Id(Long itemId);
}
