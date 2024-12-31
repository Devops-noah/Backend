package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Data
@Table(name = "information_colis")
public class InformationColis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float poids;
    private String dimensions; // Longueur x Largeur x Hauteur
    private String nature;
    private String categorie;
    private Date datePriseEnCharge;
    private String plageHoraire;

    @ManyToOne
    @JoinColumn(name = "annonce_id", nullable = false)
    private Annonce annonce; // Liée à une annonce

    @ManyToOne
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur; // L'expéditeur ayant proposé le colis
}
