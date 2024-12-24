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

    // For Voyageur
    private List<AnnonceResponse> annonces;
    private List<Voyage> voyages;

    // Expediteur-specific fields
    //private List<ColisResponse> colis;
    // Message for Expediteur or general cases
    private String message;
}
