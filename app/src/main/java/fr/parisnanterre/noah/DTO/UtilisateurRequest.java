package fr.parisnanterre.noah.DTO;

import lombok.Data;

@Data
public class UtilisateurRequest {
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
}
