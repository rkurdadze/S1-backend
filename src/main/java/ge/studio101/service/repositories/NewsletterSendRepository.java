package ge.studio101.service.repositories;

import ge.studio101.service.models.NewsletterSend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterSendRepository extends JpaRepository<NewsletterSend, Long> {
}
