package ge.studio101.service.repositories;

import ge.studio101.service.models.NewsletterSegment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterSegmentRepository extends JpaRepository<NewsletterSegment, Long> {
}
