package fr.parisnanterre.noah.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class InformationColisRequest {
    private float poids;
    private float longueur;
    private float largeur;
    private float hauteur;
    private String nature;
    private String categorie;
    private Date datePriseEnCharge;
    private String plageHoraire;
    private String message;
    private Long annonceId; // ID de l'annonce associée
    private Long expediteurId; // ID de l'utilisateur (expéditeur)
}
