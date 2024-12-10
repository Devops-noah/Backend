package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Livraison {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "expediteur_id")
    private Utilisateur expediteur;

    @ManyToOne
    @JoinColumn(name = "voyageur_id")
    private Utilisateur voyageur;

//    @ManyToOne
//    @JoinColumn(name = "receveur_id")
//    private Utilisateur receveur;

    private Date dateEnvoi;
    private Date dateReception;

    @Enumerated(EnumType.STRING)
    private StatutLivraison statut;
}
