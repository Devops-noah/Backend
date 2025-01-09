// SegmentRepository.java
package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SegmentRepository extends JpaRepository<Segment, Long> {
    // Méthode personnalisée pour rechercher des segments par point de départ et d'arrivée
    List<Segment> findByPointDepartAndPointArrivee(String pointDepart, String pointArrivee);
}
