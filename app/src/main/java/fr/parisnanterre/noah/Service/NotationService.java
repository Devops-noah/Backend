package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Notation;
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
    private NotationRepository notationRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

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
    public List<Notation> getAllNotations() {
        return notationRepository.findAll();
    }

    // Méthode pour récupérer les notations par utilisateur
    public List<Notation> getNotationsByUtilisateurId(Long utilisateurId) {
        return notationRepository.findByUtilisateurId(utilisateurId);
    }

    // Méthode pour récupérer les 3 dernières notations (si disponibles)
    public List<Notation> getLastThreeNotations() {
        return notationRepository.findTop3ByOrderByDatePublicationDesc();
    }
}
