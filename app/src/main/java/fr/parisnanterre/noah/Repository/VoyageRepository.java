// Voyage repository
package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoyageRepository extends JpaRepository<Voyage, Integer> {
    List<Voyage> findByVoyageur(Utilisateur voyageur); // Find voyages by Voyageur
    List<Voyage> findByPaysDestination(Pays paysDestination); // Find voyages by destination
    List<Voyage> findByPaysDepart(Pays paysDepart); // Find voyages by departure country
}


