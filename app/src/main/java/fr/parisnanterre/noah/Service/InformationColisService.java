package fr.parisnanterre.noah.Service;
import fr.parisnanterre.noah.DTO.DemandeRequest;
import fr.parisnanterre.noah.DTO.DemandeResponse;
import fr.parisnanterre.noah.DTO.InformationColisRequest;
import fr.parisnanterre.noah.DTO.InformationColisResponse;
import fr.parisnanterre.noah.Entity.*;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.InformationColisRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class InformationColisService {

    @Autowired
    private InformationColisRepository informationColisRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private DemandeService demandeService; // Service pour créer la demande

    @Autowired
    private NotificationService notificationService; // Service pour envoyer des notifications

    public InformationColisResponse proposerColis(InformationColisRequest colisRequest, String email, Long annonceId) throws Exception {
        // Fetch the user by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the user is a Voyageur, if not, set them as one
        if (!utilisateur.isExpediteur()) {
            utilisateur.becomeExpediteur();  // Make the user a Voyageur if they are not one
            utilisateurRepository.save(utilisateur);  // Save the updated user
        }

        // Récupérer l'annonce associée
        Annonce annonce = annonceRepository.findById(Math.toIntExact(annonceId))
                .orElseThrow(() -> new Exception("Annonce avec ID " + annonceId + " non trouvée"));

        // Créer l'entité InformationColis
        InformationColis informationColis = new InformationColis();
        informationColis.setPoids(colisRequest.getPoids());
        informationColis.setDimensions(colisRequest.getLongueur() + "x" + colisRequest.getLargeur() + "x" + colisRequest.getHauteur());
        informationColis.setNature(colisRequest.getNature());
        informationColis.setCategorie(colisRequest.getCategorie());
        informationColis.setDatePriseEnCharge(colisRequest.getDatePriseEnCharge());
        informationColis.setPlageHoraire(colisRequest.getPlageHoraire());
        informationColis.setMessage(colisRequest.getMessage());
        informationColis.setAnnonce(annonce);
        informationColis.setExpediteur(utilisateur); // 🔹 Cast à Expéditeur

        // Sauvegarder l'InformationColis dans la base de données
        InformationColis savedColis = informationColisRepository.save(informationColis);

        // ✅ Créer automatiquement la demande associée
        String expediteurEmail = utilisateur.getEmail();
        DemandeRequest demandeRequest = new DemandeRequest();
        demandeRequest.setExpediteurEmail(expediteurEmail);
        demandeRequest.setStatus(Statut.EN_ATTENTE);
        demandeRequest.setCreatedAt(new Date());

        // 🔹 Appeler le service DemandeService pour créer la demande
        DemandeResponse demandeResponse = demandeService.createDemande(demandeRequest, savedColis.getId(), expediteurEmail);

        // 🔹 Mapper l'entité en DTO
        InformationColisResponse response = mapToInformationColisResponse(savedColis);
        response.setDemande(demandeResponse); // Inclure la demande

        return response;
    }

    // 🔹 Helper method to convert InformationColis to InformationColisResponse
    InformationColisResponse mapToInformationColisResponse(InformationColis colis) {
        if (colis == null) {
            return null;
        }

        InformationColisResponse response = new InformationColisResponse();
        response.setId(colis.getId());
        response.setPoids(colis.getPoids());
        response.setDimensions(colis.getDimensions());
        response.setNature(colis.getNature());
        response.setCategorie(colis.getCategorie());
        response.setDatePriseEnCharge(colis.getDatePriseEnCharge());
        response.setPlageHoraire(colis.getPlageHoraire());
        response.setMessage(colis.getMessage());

        // 🔹 Include demande if it exists
        if (colis.getDemande() != null) {
            DemandeResponse demandeResponse = new DemandeResponse();
            demandeResponse.setId(colis.getDemande().getId());
            demandeResponse.setExpediteurId(colis.getDemande().getExpediteur().getId());
            demandeResponse.setExpediteurEmail(colis.getDemande().getExpediteur().getEmail());
            demandeResponse.setExpediteurNom(colis.getDemande().getExpediteur().getNom());
            demandeResponse.setStatus(colis.getDemande().getStatus());
            demandeResponse.setCreatedAt(colis.getDemande().getCreatedAt());
            demandeResponse.setVoyageurNom(colis.getDemande().getVoyageur().getNom());

            response.setDemande(demandeResponse);
        }

        return response;
    }


}
