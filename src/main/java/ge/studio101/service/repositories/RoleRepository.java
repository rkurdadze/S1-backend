package ge.studio101.service.repositories;

import ge.studio101.service.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<UserRole, Long> {
}
