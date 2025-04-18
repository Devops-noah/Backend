package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Demande;
import fr.parisnanterre.noah.Entity.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeRepository extends JpaRepository<Demande, Long> {
    List<Demande> findByVoyageurId(Long voyageurId);
    List<Demande> findByExpediteurId(Long expediteurId);
    List<Demande> findByStatus(Statut status);

}
