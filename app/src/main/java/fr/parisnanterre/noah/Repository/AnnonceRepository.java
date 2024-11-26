package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.Pays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnnonceRepository extends JpaRepository<Annonce, Integer> {
    @Query("SELECT a FROM Annonce a LEFT JOIN FETCH a.voyage")
    List<Annonce> findAllWithVoyage();

    List<Annonce> findByPaysDepart(Pays paysDepart);
    List<Annonce> findByPaysDestination(Pays paysDest);
}
