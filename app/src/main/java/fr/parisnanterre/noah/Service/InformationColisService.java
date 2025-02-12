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
        // Récupérer l'utilisateur authentifié par email
        Utilisateur expediteur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));

        // 🔹 Vérifier si l'utilisateur a le type EXPEDITEUR
        boolean isExpediteur = expediteur.getUserTypes().stream()
                .anyMatch(userType -> userType.equals(UserType.EXPEDITEUR));

        if (!isExpediteur) {
            throw new RuntimeException("L'utilisateur n'est pas un expéditeur");
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
        informationColis.setExpediteur(expediteur); // 🔹 Cast à Expéditeur

        // Sauvegarder l'InformationColis dans la base de données
        InformationColis savedColis = informationColisRepository.save(informationColis);

        // ✅ Créer automatiquement la demande associée
        String expediteurEmail = expediteur.getEmail();
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

        return response;
    }

}
