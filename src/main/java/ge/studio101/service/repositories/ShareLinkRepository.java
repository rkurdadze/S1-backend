package ge.studio101.service.repositories;

import ge.studio101.service.models.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareLinkRepository extends JpaRepository<ShareLink, String> {
}
