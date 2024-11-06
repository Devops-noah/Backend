// Voyage repository
package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoyageRepository extends JpaRepository<Voyage, Integer> {
    List<Voyage> findByVoyageur(Utilisateur voyageur); // Find voyages by user (voyageur)
    List<Voyage> findByDestination(Pays destination); // Find voyages by destination country
}

