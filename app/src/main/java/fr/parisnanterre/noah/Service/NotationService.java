package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.NotationResponse;
import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.Entity.StatutNotation;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.NotationRepository;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import fr.parisnanterre.noah.DTO.NotationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotationService {

    @Autowired
    private final NotationRepository notationRepository;

    @Autowired
    private final UtilisateurRepository utilisateurRepository;

    public NotationService(NotationRepository notationRepository, UtilisateurRepository utilisateurRepository) {
        this.notationRepository = notationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Méthode pour créer une notation
    public Notation createNotation(NotationRequest notationRequest) throws Exception {
        // Vérifier si l'utilisateur existe
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(notationRequest.getUtilisateurId());
        if (!utilisateurOpt.isPresent()) {
            throw new Exception("Utilisateur non trouvé");
        }

        Utilisateur utilisateur = utilisateurOpt.get();


        // Créer une nouvelle notation
        Notation newNotation = new Notation();
        newNotation.setNote(notationRequest.getNote());
        newNotation.setCommentaire(notationRequest.getCommentaire());
        newNotation.setDatePublication(notationRequest.getDatePublication());
        newNotation.setUtilisateur(utilisateur);

        // Enregistrer et retourner la nouvelle notation
        return notationRepository.save(newNotation);
    }

    // Méthode pour récupérer toutes les notations
    public List<NotationResponse> getAllNotationsWithConditionalComments() {
        try {
            // Fetch all notations from the repository
            List<Notation> notations = notationRepository.findAll();

            // Map each `Notation` entity to `NotationResponse` DTO
            return notations.stream()
                    .map(notation -> {
                        NotationResponse response = new NotationResponse();
                        response.setUtilisateurId(notation.getUtilisateur().getId());
                        response.setUserName(notation.getUtilisateur().getNom());
                        response.setUserFirstName(notation.getUtilisateur().getPrenom());
                        response.setNote(notation.getNote());
                        //response.setCommentaire(notation.getCommentaire());
                        response.setDatePublication(notation.getDatePublication().toString()); // Format date as String
                        return response;
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des notations", e);
        }
    }

    public List<NotationResponse> getAllNotationsWithApprovedComments() {
        try {
            // Map each `Notation` entity to `NotationResponse` DTO
            return notationRepository.findAll().stream()
                    .filter(notation -> notation.getStatus() == StatutNotation.APPROVED) // Include only approved notations
                    .map(notation -> {
                        NotationResponse response = new NotationResponse();
                        response.setUtilisateurId(notation.getUtilisateur().getId());
                        response.setUserName(notation.getUtilisateur().getNom());
                        response.setUserFirstName(notation.getUtilisateur().getPrenom());
                        response.setNote(notation.getNote());
                        response.setCommentaire(notation.getCommentaire());
                        response.setDatePublication(notation.getDatePublication().toString()); // Format date as String

                        return response;
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des notations", e);
        }
    }




    // Méthode pour récupérer les notations par utilisateur
    public List<Notation> getNotationsByUtilisateurId(Long utilisateurId) {
        return notationRepository.findByUtilisateurId(utilisateurId);
    }

    // Service method to fetch the last three approved notations
    public List<NotationResponse> getLastThreeNotationsWithApprovedComments() {
        try {
            // Fetch the 3 most recent approved notations
            List<Notation> approvedNotations = notationRepository.findTop3ByStatusOrderByDatePublicationDesc(StatutNotation.APPROVED);

            // Map the `Notation` entities to `NotationResponse` DTOs
            return approvedNotations.stream()
                    .map(notation -> {
                        NotationResponse response = new NotationResponse();
                        response.setUtilisateurId(notation.getUtilisateur().getId());
                        response.setUserName(notation.getUtilisateur().getNom());
                        response.setUserFirstName(notation.getUtilisateur().getPrenom());
                        response.setNote(notation.getNote());
                        response.setCommentaire(notation.getCommentaire()); // Only approved comments are fetched
                        response.setDatePublication(notation.getDatePublication().toString());

                        return response;
                    })
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des dernières notations approuvées", e);
        }
    }


}

