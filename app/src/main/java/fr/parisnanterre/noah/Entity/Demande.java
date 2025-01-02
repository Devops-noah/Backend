package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "information_colis_id", nullable = false)
    private InformationColis informationColis; // Lié à un colis

    @ManyToOne
    @JoinColumn(name = "voyageur_id", nullable = false)
    private Utilisateur voyageur; // Voyageur lié à la demande

    @ManyToOne
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur; // Expéditeur qui a initié la demande

    @Enumerated(EnumType.STRING)
    private Statut status; // Statut de la demande : EN_ATTENTE, ACCEPTE, REFUSE

    private Date createdAt;
}
