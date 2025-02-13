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

    public InformationColisResponse(Long id, float poids, String dimensions, String nature, String categorie, Date datePriseEnCharge, String plageHoraire, String message, DemandeResponse demande) {
        this.id = id;
        this.poids = poids;
        this.dimensions = dimensions;
        this.nature = nature;
        this.categorie = categorie;
        this.datePriseEnCharge = datePriseEnCharge;
        this.plageHoraire = plageHoraire;
        this.message = message;
        this.demande = demande;
    }

    public InformationColisResponse() {}
}
