package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Annonce;
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

    @Query("SELECT a FROM Annonce a JOIN FETCH a.voyage v WHERE a.approved = true AND a.suspended = false")
    List<Annonce> findByVoyageur(Voyageur voyageur);

    @Query("SELECT a FROM Annonce a " +
            "JOIN FETCH a.voyage v " +
            "JOIN FETCH v.paysDepart " +
            "JOIN FETCH v.paysDestination " +
            "WHERE a.approved = true AND a.suspended = false")
    List<Annonce> findActiveAnnoncesWithPays();
}
