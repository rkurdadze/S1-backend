package ge.studio101.service.repositories;

import ge.studio101.service.models.NewsletterDraft;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsletterDraftRepository extends JpaRepository<NewsletterDraft, Long> {
}
