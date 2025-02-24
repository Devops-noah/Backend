package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.AnnonceResponse;
import fr.parisnanterre.noah.DTO.UtilisateurProfileResponse;
import fr.parisnanterre.noah.Entity.*;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Set;

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
        // Fetch user by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        // Create response object
        UtilisateurProfileResponse profile = new UtilisateurProfileResponse();

        // Common user info
        profile.setId(utilisateur.getId());
        profile.setNom(utilisateur.getNom());
        profile.setPrenom(utilisateur.getPrenom());
        profile.setEmail(utilisateur.getEmail());
        profile.setTelephone(utilisateur.getTelephone());
        profile.setAdresse(utilisateur.getAdresse());
        profile.setAdmin(utilisateur.isAdmin());

        // Handle profile image
        if (utilisateur.getProfileImage() != null) {
            byte[] profileImage = utilisateur.getProfileImage().getBytes();
            byte[] base64Image = Base64.getEncoder().encodeToString(profileImage).getBytes();
            profile.setProfileImage(base64Image);
        } else {
            profile.setProfileImage("https://picsum.photos/50/50".getBytes());
        }

        // Get unread notifications count
        profile.setNotificationCount(utilisateur.getNotificationCount());

        // Determine if the user should be a Voyageur or Expediteur
        boolean hasAnnonces = !utilisateur.getAnnonces().isEmpty();
        boolean hasColis = !utilisateur.getDemandes().isEmpty();
        Set<UserType> userType = utilisateur.getUserTypes(); // Get current user type

        if (hasAnnonces || !utilisateur.isVoyageur()) {
            // If user has annonces or is not yet a Voyageur, set them as one
            utilisateur.becomeVoyageur();
            utilisateurRepository.save(utilisateur); // Persist change
        } else if (hasColis || !utilisateur.isExpediteur()) {
            // If user has colis or is not yet an Expediteur, set them as one
            utilisateur.becomeExpediteur();
            utilisateurRepository.save(utilisateur); // Persist change
        }

        // ✅ Construct UserTypeResponse
        UtilisateurProfileResponse.UserTypeResponse userTypeResponse = new UtilisateurProfileResponse.UserTypeResponse();
        userTypeResponse.setUserId(utilisateur.getId()); // Set userId
        userTypeResponse.setDType(utilisateur.getUserTypes().stream()
                .map(Enum::name) // Convert ENUM to String
                .toList()); // Convert Set<UserType> to List<String>

        // ✅ Set userTypes in the profile response
        profile.setUserTypes(userTypeResponse);

        // ✅ Set demandes and notations properly
        profile.setDemandes(utilisateur.getDemandes());
        profile.setNotations(utilisateur.getNotations());

        // Populate annonces if user is a Voyageur
        if (utilisateur.isVoyageur()) {
            List<AnnonceResponse> annonces = utilisateur.getAnnonces().stream().map(annonce -> {
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
        }

        // Populate voyages if user is a Voyageur
        if (utilisateur.isVoyageur()) {
            List<Voyage> voyages = utilisateur.getVoyages().stream().map(voyage -> {
                Voyage voyageResponse = new Voyage();
                voyageResponse.setId(voyage.getId());
                voyageResponse.setDateDepart(voyage.getDateDepart());
                voyageResponse.setDateArrivee(voyage.getDateArrivee());
                voyageResponse.setPaysDepart(voyage.getPaysDepart() != null ? voyage.getPaysDepart() : null);
                voyageResponse.setPaysDestination(voyage.getPaysDestination() != null ? voyage.getPaysDestination() : null);
                return voyageResponse;
            }).toList();
            profile.setVoyages(voyages);
        }

        // Handle Expediteur case
        if (utilisateur.isExpediteur()) {
            profile.setMessage("L'utilisateur est un expediteur. Les colis ne sont pas disponibles pour le moment.");
        }

        return profile;
    }



    public void updateUserProfileImage(Long userId, String imgurImageUrl) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));
        utilisateur.setProfileImage(imgurImageUrl);  // Store the Imgur URL instead of byte[]
        utilisateurRepository.save(utilisateur);
    }

    public String getUserProfileImageUrl(Long userId) {
        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));
        return utilisateur.getProfileImage();  // Get the URL
    }


    public Long getUserIdByEmail(String email) {
        // Find the user by email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        // Return the user's ID
        return utilisateur.getId();
    }


    // Méthode pour récupérer le nom de l'expéditeur par ID
    public String getExpediteurNom(Long expediteurId) {
        // Vérifier si l'utilisateur avec l'ID donné existe
        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> new RuntimeException("Expéditeur non trouvé"));
        System.out.println("hawa uuuuuuuuuuuu: " + expediteur.getNom());

        return expediteur.getNom();  // Retourner le nom de l'expéditeur
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
