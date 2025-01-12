package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.DemandeRequest;
import fr.parisnanterre.noah.Entity.*;
import fr.parisnanterre.noah.DTO.DemandeResponse; // Importer la classe DemandeResponse
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
    private final NotificationService notificationService;

    // Récupérer les demandes par voyageur
    public List<Demande> getDemandesByVoyageur(String email) {
        Utilisateur voyageur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Voyageur non trouvé"));
        return demandeRepository.findByVoyageurId(voyageur.getId());
    }

    // Récupérer les demandes pour un expéditeur spécifique
    public List<Demande> getDemandesByExpediteur(String expediteurEmail) {
        Utilisateur expediteur = utilisateurRepository.findByEmail(expediteurEmail)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        return demandeRepository.findByExpediteurId(expediteur.getId());
    }

    // Créer une nouvelle demande
    @Transactional
    public DemandeResponse createDemande(DemandeRequest demandeRequest, Long colisId, String expediteurEmail) {
        System.out.println("blalalalalalalal");
        // Récupérer le colis à partir de l'ID
        InformationColis colis = informationColisRepository.findById(colisId)
                .orElseThrow(() -> new RuntimeException("Colis non trouvé"));
        System.out.println("test colis value: " + colis.getAnnonce().getVoyageur().getNom());

        // Récupérer l'expéditeur authentifié à partir de l'email
        Utilisateur expediteur = utilisateurRepository.findByEmail(expediteurEmail)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));

        // Vérifier si l'expéditeur est bien autorisé à créer une demande pour ce colis
        if (!colis.getExpediteur().equals(expediteur)) {
            throw new RuntimeException("L'expéditeur n'est pas autorisé à créer une demande pour ce colis");
        }

        // Créer la demande
        Demande demande = new Demande();
        demande.setInformationColis(colis); // Lien entre la demande et le colis
        demande.setExpediteur(expediteur); // L'expéditeur qui a proposé le colis
        demande.setStatus(Statut.EN_ATTENTE); // Par défaut, en attente
        demande.setCreatedAt(new Date());
        demande.setVoyageur(colis.getAnnonce().getVoyageur()); // Le voyageur pour lequel la demande est proposée

        System.out.println("demande : " + demande);

        // Sauvegarder la demande dans la base de données
        Demande savedDemande = demandeRepository.save(demande);

        // Créer automatiquement la notification associée à la demande
        notificationService.createNotification(savedDemande.getId()); // Crée la notification pour la demande

        // Retourner la réponse sous forme de DTO
        DemandeResponse response = new DemandeResponse();
        response.setId(savedDemande.getId());
        response.setExpediteurEmail(savedDemande.getExpediteur().getEmail());
        response.setStatus(savedDemande.getStatus());
        response.setCreatedAt(savedDemande.getCreatedAt());
        response.setVoyageurNom(String.valueOf(colis.getAnnonce().getVoyageur().getNom()));
        System.out.println("reponse demande: " + response);

        return response;
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
