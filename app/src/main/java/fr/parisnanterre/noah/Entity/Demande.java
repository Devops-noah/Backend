package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @JoinColumn(name = "information_colis_id", nullable = false)
    private InformationColis informationColis; // Lié à un colis

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "voyageur_id", nullable = false)
    private Utilisateur voyageur; // Voyageur lié à la demande

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur; // Expéditeur qui a initié la demande

    @Enumerated(EnumType.STRING)
    private Statut status; // Statut de la demande : EN_ATTENTE, ACCEPTE, REFUSE

    private Date createdAt;

}
