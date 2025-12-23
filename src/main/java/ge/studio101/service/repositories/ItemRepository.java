package ge.studio101.service.repositories;

import ge.studio101.service.models.Item;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Override
    @EntityGraph(attributePaths = {"colors", "itemTags"})
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT i FROM Item i")
    List<Item> findAll();
}

