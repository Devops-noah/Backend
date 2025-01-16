package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.Entity.StatutNotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotationRepository extends JpaRepository<Notation, Long> {

    // Méthode pour trouver les notations par utilisateur
    List<Notation> findByUtilisateurId(Long utilisateurId);

    // Méthode pour récupérer les 3 dernières notations par ordre de date
    List<Notation> findTop3ByStatusOrderByDatePublicationDesc(StatutNotation status);

    List<Notation> findByStatus(StatutNotation status);
}
