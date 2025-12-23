package ge.studio101.service.repositories;

import ge.studio101.service.dto.PhotoIdProjection;
import ge.studio101.service.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByColor_Id(Long colorId);

    @Query("SELECT p.id as id, p.color.id as colorId FROM Photo p WHERE p.color.id IN :colorIds")
    List<PhotoIdProjection> findIdsByColorIdIn(@Param("colorIds") Collection<Long> colorIds);
}