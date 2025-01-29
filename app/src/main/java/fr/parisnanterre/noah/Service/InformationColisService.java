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
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur avec email " + email + " non trouvé"));

        // Vérifier que l'utilisateur est bien un expéditeur
        if (!(utilisateur instanceof Expediteur)) {
            throw new RuntimeException("Seul un expéditeur peut proposer un colis");
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
        informationColis.setAnnonce(annonce);
        informationColis.setExpediteur((Expediteur) utilisateur); // Définir l'expéditeur

        // Sauvegarder l'InformationColis dans la base de données
        // Créer et sauvegarder l'information colis
        InformationColis savedColis = informationColisRepository.save(informationColis);

// Créer automatiquement la demande associée
        String expediteurEmail = utilisateur.getEmail();  // Utiliser l'email de l'expéditeur
        DemandeRequest demandeRequest = new DemandeRequest();
        demandeRequest.setExpediteurEmail(expediteurEmail);  // L'email de l'expéditeur
        demandeRequest.setStatus(Statut.EN_ATTENTE);  // Le statut de la demande (par défaut)
        demandeRequest.setCreatedAt(new Date());  // Date de création de la demande

// Appeler le service DemandeService pour créer la demande
        DemandeResponse demandeResponse = demandeService.createDemande(demandeRequest, savedColis.getId(), expediteurEmail);

// Retourner la réponse avec l'information colis et la demande créée
        InformationColisResponse response = new InformationColisResponse();
        response.setId(savedColis.getId());
        response.setPoids(savedColis.getPoids());
        response.setDimensions(savedColis.getDimensions());
        response.setNature(savedColis.getNature());
        response.setCategorie(savedColis.getCategorie());
        response.setDatePriseEnCharge(savedColis.getDatePriseEnCharge());
        response.setPlageHoraire(savedColis.getPlageHoraire());
        response.setDemande(demandeResponse); // Inclure la demande dans la réponse

        return response;
    }
}
