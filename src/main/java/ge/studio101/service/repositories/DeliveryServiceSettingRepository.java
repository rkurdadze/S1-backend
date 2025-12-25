package ge.studio101.service.repositories;

import ge.studio101.service.models.DeliveryServiceSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryServiceSettingRepository extends JpaRepository<DeliveryServiceSetting, String> {
}
