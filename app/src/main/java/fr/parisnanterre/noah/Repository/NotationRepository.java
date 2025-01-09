package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Livraison;
import fr.parisnanterre.noah.Entity.Notation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotationRepository extends JpaRepository<Notation, Integer> {
    /*List<Notation> findByLivraison(Livraison livraison);*/
}

