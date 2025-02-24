package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.Demande;
import fr.parisnanterre.noah.Entity.Notation;
import fr.parisnanterre.noah.Entity.UserType;
import fr.parisnanterre.noah.Entity.Voyage;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class UtilisateurProfileResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String type;
    private byte[] profileImage;
    private UserTypeResponse userTypes; // ✅ Updated field
    private boolean isAdmin;

    // Pour le type Voyageur
    private List<AnnonceResponse> annonces;
    private List<Voyage> voyages;
    private List<Demande> demandes;
    private List<Notation> notations;

    // Message spécifique pour l'Expediteur ou autres cas généraux
    private String message;

    // Champ ajouté pour le nombre de notifications non lues
    private int notificationCount;

    // ✅ Inner class for userTypes formatting
    @Data
    public static class UserTypeResponse {
        private List<String> dType;
        private Long userId;
    }


}
