package ge.studio101.service.repositories;

import ge.studio101.service.dto.InventoryDTO;
import ge.studio101.service.models.Inventory;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findAllById_In(@NotNull List<Long> colors);

    List<Inventory> findAllByColor_IdIn(List<Long> colorIds);
}

