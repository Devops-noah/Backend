package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Integer> {
}
