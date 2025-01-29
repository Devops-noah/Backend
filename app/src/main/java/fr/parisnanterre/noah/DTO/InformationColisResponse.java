package fr.parisnanterre.noah.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class InformationColisResponse {
    private Long id;
    private float poids;
    private String dimensions;
    private String nature;
    private String categorie;
    private Date datePriseEnCharge;
    private String plageHoraire;
    private String message; // Message de confirmation ou d'erreur
    private DemandeResponse demande; // Ajouter ce champ pour inclure la demande dans la réponse
}
