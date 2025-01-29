package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.DemandeRequest;
import fr.parisnanterre.noah.DTO.InformationColisResponse;
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
import java.util.stream.Collectors;

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

    public List<DemandeResponse> getDemandesByExpediteur(String expediteurEmail) {
        // Retrieve the user by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(expediteurEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Check if the user type is "expediteur"
        if (!(utilisateur instanceof Expediteur)) {
            // If not an expediteur, throw an error
            throw new RuntimeException("Vous devez être connecté en tant qu'expéditeur pour accéder aux demandes.");
        }

        // Fetch demandes for the expediteur
        List<Demande> demandes = demandeRepository.findByExpediteurId(utilisateur.getId());

        // Map each Demande entity to a DemandeResponse DTO
        return demandes.stream().map(demande -> {
            DemandeResponse demandeResponse = new DemandeResponse();
            demandeResponse.setId(demande.getId());
            demandeResponse.setExpediteurId(demande.getExpediteur().getId());
            demandeResponse.setExpediteurEmail(demande.getExpediteur().getEmail());
            demandeResponse.setExpediteurNom(demande.getExpediteur().getNom());
            demandeResponse.setStatus(demande.getStatus());
            demandeResponse.setCreatedAt(demande.getCreatedAt());

            // Set voyageur name (if a voyageur has been assigned)
            if (demande.getVoyageur() != null) {
                demandeResponse.setVoyageurNom(demande.getVoyageur().getNom());
            } else {
                demandeResponse.setVoyageurNom("Aucun voyageur assigné");
            }

            // Map InformationColis to InformationColisResponse
            InformationColisResponse colisResponse = new InformationColisResponse();
            if (demande.getInformationColis() != null) {
                colisResponse.setId(demande.getInformationColis().getId());
                colisResponse.setPoids(demande.getInformationColis().getPoids());
                colisResponse.setDimensions(demande.getInformationColis().getDimensions());
                colisResponse.setNature(demande.getInformationColis().getNature());
                colisResponse.setCategorie(demande.getInformationColis().getCategorie());
                colisResponse.setDatePriseEnCharge(demande.getInformationColis().getDatePriseEnCharge());
                colisResponse.setPlageHoraire(demande.getInformationColis().getPlageHoraire());
                colisResponse.setMessage("Colis lié à la demande");
            } else {
                colisResponse.setMessage("Aucun colis associé à cette demande");
            }

            demandeResponse.setInformationColis(colisResponse);
            System.out.println("demande response: " + demandeResponse);

            return demandeResponse;
        }).collect(Collectors.toList());
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
        System.out.println("reponse demande: " + response);
        response.setId(savedDemande.getId());
        response.setExpediteurEmail(savedDemande.getExpediteur().getEmail());
        response.setStatus(savedDemande.getStatus());
        response.setCreatedAt(savedDemande.getCreatedAt());
        response.setVoyageurNom(String.valueOf(colis.getAnnonce().getVoyageur().getNom()));

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
