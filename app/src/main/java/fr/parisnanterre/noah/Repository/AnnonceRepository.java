package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.Pays;
import fr.parisnanterre.noah.Entity.Voyageur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Integer> {
    @Query("SELECT a FROM Annonce a LEFT JOIN FETCH a.voyage")
    List<Annonce> findAllWithVoyage();

    List<Annonce> findByVoyagePaysDepartNom(String paysDepartNom);

    List<Annonce> findByVoyagePaysDestinationNom(String paysDestinationNom);

    // Find all annonces associated with a specific Voyageur
    // Fetch all annonces where approved = true and suspended = false
    @Query("SELECT a FROM Annonce a JOIN FETCH a.voyage v WHERE a.approved = true AND a.suspended = false")
    List<Annonce> findByVoyageur(Voyageur voyageur);

    // Custom query to delete all annonces associated with a specific Voyageur
    void deleteByVoyageur(Voyageur voyageur);


}
