package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "expediteur_id")
    private Utilisateur expediteur;

    @ManyToOne
    private Annonce annonce;

    @Enumerated(EnumType.STRING)
    private Statut statut;

}
