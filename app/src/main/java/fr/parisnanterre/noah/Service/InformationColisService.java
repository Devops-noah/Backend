package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.InformationColisRequest;
import fr.parisnanterre.noah.DTO.InformationColisResponse;
import fr.parisnanterre.noah.Entity.*;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import fr.parisnanterre.noah.Repository.InformationColisRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InformationColisService {

    @Autowired
    private InformationColisRepository informationColisRepository;

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NotificationService notificationService; // Ajout du service Notification

    public InformationColisResponse proposerColis(InformationColisRequest colisRequest, String email, Long annonceId) throws Exception {
        // Retrieve the logged-in user by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
        System.out.println("utilisateur value: " + utilisateur);

        // Ensure the user is an Expediteur
        if (!(utilisateur instanceof Expediteur)) {
            throw new RuntimeException("Only an Expediteur can propose a colis");
        }

        // Validate the existence of the related annonce
        Annonce annonce = annonceRepository.findById(Math.toIntExact(annonceId))
                .orElseThrow(() -> new Exception("Voyage with ID " + annonceId + " not found"));

        // Create the InformationColis entity
        InformationColis informationColis = new InformationColis();
        informationColis.setPoids(colisRequest.getPoids());
        informationColis.setDimensions(
                colisRequest.getLongueur() + "x" + colisRequest.getLargeur() + "x" + colisRequest.getHauteur()
        );
        informationColis.setNature(colisRequest.getNature());
        informationColis.setCategorie(colisRequest.getCategorie());
        informationColis.setDatePriseEnCharge(colisRequest.getDatePriseEnCharge());
        informationColis.setPlageHoraire(colisRequest.getPlageHoraire());
        informationColis.setAnnonce(annonce);
        informationColis.setExpediteur((Expediteur) utilisateur); // Set the expediteur

        // Save the entity in the database
        InformationColis savedColis = informationColisRepository.save(informationColis);

        // Create a notification for the voyageur
        Utilisateur voyageur = annonce.getVoyageur();
        String notificationMessage = "Nouvelle demande pour votre annonce publiée le " + annonce.getDatePublication();

        notificationService.createNotification(voyageur, notificationMessage, savedColis.getId());

        // Create a response DTO
        InformationColisResponse response = new InformationColisResponse();
        response.setId(savedColis.getId());
        response.setPoids(savedColis.getPoids());
        response.setDimensions(savedColis.getDimensions());
        response.setNature(savedColis.getNature());
        response.setCategorie(savedColis.getCategorie());
        response.setDatePriseEnCharge(savedColis.getDatePriseEnCharge());
        response.setPlageHoraire(savedColis.getPlageHoraire());
        response.setMessage("Colis proposé avec succès !");
        return response;
    }
}
