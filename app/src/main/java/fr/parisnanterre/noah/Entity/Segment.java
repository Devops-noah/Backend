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

    @ManyToOne
    @JoinColumn(name = "voyage_id", nullable = false)
    private Voyage voyage; // Voyage associé à ce segment
}
