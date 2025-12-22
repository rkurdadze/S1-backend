package ge.studio101.service.repositories;

import ge.studio101.service.models.CollectionEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollectionRepository extends JpaRepository<CollectionEntry, Long> {
}
