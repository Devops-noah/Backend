package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Demande;
import fr.parisnanterre.noah.Entity.InformationColis;
import fr.parisnanterre.noah.Entity.Statut;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.DemandeRepository;
import fr.parisnanterre.noah.Repository.InformationColisRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemandeService {

    private final DemandeRepository demandeRepository;
    private final InformationColisRepository informationColisRepository;
    private final UtilisateurRepository utilisateurRepository;

    // Récupérer les demandes par voyageur
    public List<Demande> getDemandesByVoyageur(String email) {
        Utilisateur voyageur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Voyageur non trouvé"));
        return demandeRepository.findByVoyageurId(voyageur.getId());
    }

    // Créer une nouvelle demande
    @Transactional
    public Demande createDemande(Long colisId, Long voyageurId, String expediteurEmail) {
        // Récupérer le colis
        InformationColis colis = informationColisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé"));

        // Récupérer le voyageur
        Utilisateur voyageur = utilisateurRepository.findById(voyageurId)
                .orElseThrow(() -> new RuntimeException("Voyageur non trouvé"));

        // Récupérer l'expéditeur
        Utilisateur expediteur = utilisateurRepository.findByEmail(expediteurEmail)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));

        // Créer la demande
        Demande demande = new Demande();
        demande.setInformationColis(colis);
        demande.setVoyageur(voyageur);
        demande.setExpediteur(expediteur);
        demande.setStatus(Statut.EN_ATTENTE); // Par défaut, en attente
        demande.setCreatedAt(new Date());

        return demandeRepository.save(demande);
    }


    // Mettre à jour le statut d'une demande
    @Transactional
    public Demande updateStatus(Long demandeId, Statut status) {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        if (status != Statut.ACCEPTE && status != Statut.REFUSE) {
            throw new IllegalArgumentException("Statut invalide");
        }

        demande.setStatus(status);
        return demandeRepository.save(demande);
    }
}