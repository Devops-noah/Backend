package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;


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

    @Column(nullable = true)
    private Long chaineId; // ID de la chaîne associée (null si non utilisé)

    @ManyToOne
    @JoinColumn(name = "annonce_id", nullable = true) // Relie un Segment à une Annonce
    private Annonce annonce;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDepart; // Date de départ

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateArrivee; // Date d'arrivée
}
