package ge.studio101.service.repositories;

import ge.studio101.service.models.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {
    List<Size> findAllByOrderByIdAsc();

    Optional<Size> findByName(String name);
}
