package ge.studio101.service.repositories;

import ge.studio101.service.models.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}
