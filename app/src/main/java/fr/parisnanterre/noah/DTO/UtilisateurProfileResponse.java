package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.Voyage;
import lombok.Data;

import java.util.List;

@Data
public class UtilisateurProfileResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private String type;

    // Pour le type Voyageur
    private List<AnnonceResponse> annonces;
    private List<Voyage> voyages;

    // Message spécifique pour l'Expediteur ou autres cas généraux
    private String message;

    // Champ ajouté pour le nombre de notifications non lues
    private int notificationCount;


}
