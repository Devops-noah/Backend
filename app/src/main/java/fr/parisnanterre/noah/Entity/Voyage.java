package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Voyage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date dateDepart;
    private Date dateArrivee;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private Pays destination;

    @ManyToOne
    @JoinColumn(name = "voyageur_id")
    private Utilisateur voyageur;
}
