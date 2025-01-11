package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Segment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pointDepart;  // Pays de départ
    private String pointArrivee; // Pays d'arrivée

    @ManyToOne
    @JoinColumn(name = "voyageur_id", nullable = true)
    private Utilisateur voyageur; // Voyageur ayant créé ce segment

    private String etat; // État du segment : "Disponible", "Réservé", etc.

    @Column(nullable = true)
    private Long chaineId; // ID de la chaîne associée (null si non utilisé)

    private Long voyageId; // ID du voyage (au lieu de l'entité Voyage)

    private Long annonceId; // ID de l'annonce (au lieu de l'entité Annonce)

    // Getter et Setter pour 'voyageId'
    public Long getVoyageId() {
        return voyageId;
    }

    public void setVoyageId(Long voyageId) {
        this.voyageId = voyageId;
    }

    // Getter et Setter pour 'annonceId'
    public Long getAnnonceId() {
        return annonceId;
    }

    public void setAnnonceId(Long annonceId) {
        this.annonceId = annonceId;
    }
}
