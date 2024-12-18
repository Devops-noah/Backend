/*package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Demande;
import fr.parisnanterre.noah.Entity.Statut;
import fr.parisnanterre.noah.Repository.DemandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeService {

    private final DemandeRepository demandeRepository;

    public void creerDemande(Demande demande) {
        demandeRepository.save(demande);
    }

    public void repondreDemande(Long demandeId, String statut) {
        Demande demande = demandeRepository.findById(demandeId).orElseThrow();
        demande.setStatut(statut.equalsIgnoreCase("ACCEPTE") ? Statut.ACCEPTE : Statut.REFUSE);
        demandeRepository.save(demande);
    }

    public List<Demande> getDemandesParExpediteur(Long expediteurId) {
        return demandeRepository.findByExpediteurId(expediteurId);
    }

    public List<Demande> getDemandesParVoyageur(Long voyageurId) {
        return demandeRepository.findByVoyageurId(voyageurId);
    }
}
*/