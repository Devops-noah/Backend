package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.AnnonceResponse;
import fr.parisnanterre.noah.DTO.UtilisateurProfileResponse;
import fr.parisnanterre.noah.Entity.*;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ImageServiceImpl imageServiceImpl;

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public UtilisateurProfileResponse getUserProfile(String email) {
        // Récupérer l'utilisateur par email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        // Créer un objet de réponse
        UtilisateurProfileResponse profile = new UtilisateurProfileResponse();

        // Remplir les informations communes pour tous les utilisateurs
        profile.setId(utilisateur.getId());
        profile.setNom(utilisateur.getNom());
        profile.setPrenom(utilisateur.getPrenom());
        profile.setEmail(utilisateur.getEmail());
        profile.setTelephone(utilisateur.getTelephone());
        profile.setAdresse(utilisateur.getAdresse());
        profile.setProfileImageUrl(utilisateur.getProfileImageUrl()); // Include the image URL


        // Récupérer le nombre de notifications non lues pour l'utilisateur
        int notificationCount = utilisateur.getNotificationCount();  // Le compteur de notifications non lues
        profile.setNotificationCount(notificationCount);

        // Déterminer le type d'utilisateur et remplir les données correspondantes
        if (utilisateur instanceof Voyageur voyageur) {
            profile.setType("voyageur");

            // Mapper les annonces pour le voyageur
            List<AnnonceResponse> annonces = voyageur.getAnnonces().stream().map(annonce -> {
                AnnonceResponse annonceResponse = new AnnonceResponse();
                annonceResponse.setId(annonce.getId());
                annonceResponse.setDatePublication(annonce.getDatePublication());
                annonceResponse.setPoidsDisponible(annonce.getPoidsDisponible());

                if (annonce.getVoyage() != null) {
                    annonceResponse.setDateDepart(annonce.getVoyage().getDateDepart());
                    annonceResponse.setDateArrivee(annonce.getVoyage().getDateArrivee());
                    annonceResponse.setPaysDepart(annonce.getVoyage().getPaysDepart() != null ? annonce.getVoyage().getPaysDepart().getNom() : null);
                    annonceResponse.setPaysDestination(annonce.getVoyage().getPaysDestination() != null ? annonce.getVoyage().getPaysDestination().getNom() : null);
                    annonceResponse.setVoyageId(annonce.getVoyage().getId());
                }
                return annonceResponse;
            }).toList();
            profile.setAnnonces(annonces);

            // Mapper les voyages pour le voyageur
            List<Voyage> voyages = voyageur.getVoyages().stream().map(voyage -> {
                Voyage voyageResponse = new Voyage();
                voyageResponse.setId(voyage.getId());
                voyageResponse.setDateDepart(voyage.getDateDepart());
                voyageResponse.setDateArrivee(voyage.getDateArrivee());
                voyageResponse.setPaysDepart(voyage.getPaysDepart() != null ? voyage.getPaysDepart() : null);
                voyageResponse.setPaysDestination(voyage.getPaysDestination() != null ? voyage.getPaysDestination() : null);
                return voyageResponse;
            }).toList();
            profile.setVoyages(voyages);

        } else if (utilisateur instanceof Expediteur) {
            profile.setType("expediteur");
            profile.setMessage("L'utilisateur est un expediteur. Les colis ne sont pas disponibles pour le moment.");
        } else if (utilisateur instanceof AdminType) {
            profile.setType("admin");
        }

        return profile;
    }


    public void updateUserProfileImage(Long userId, String imageUrl) {
        // Find the user by ID
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        // Update the profile image URL
        utilisateur.setProfileImageUrl(imageUrl);

        // Save the updated user entity
        utilisateurRepository.save(utilisateur);
    }

    public Long getUserIdByEmail(String email) {
        // Find the user by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        // Return the user's ID
        return utilisateur.getId();
    }

    public String getProfileImageByUserId(Long userId) {
        return utilisateurRepository.findById(userId)
                .map(Utilisateur::getProfileImageUrl)
                .orElse(null);
    }

    @Transactional
    public Utilisateur updateUtilisateur(Long userId, String nom, String prenom, String telephone, String adresse) {
        // Find the user by ID
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        // Update the fields if provided
        if (nom != null && !nom.isEmpty()) utilisateur.setNom(nom);
        if (prenom != null && !prenom.isEmpty()) utilisateur.setPrenom(prenom);
        if (telephone != null && !telephone.isEmpty()) utilisateur.setTelephone(telephone);
        if (adresse != null && !adresse.isEmpty()) utilisateur.setAdresse(adresse);

        // Save the updated user
        return utilisateurRepository.save(utilisateur);
    }

}
