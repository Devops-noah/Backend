package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double poids;
    private Double prix;

    @Temporal(TemporalType.DATE)
    private Date dateCreation;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = new Date();
    }

    @ManyToOne
    @JoinColumn(name = "expediteur_id")
    private Utilisateur expediteur;

    @ManyToOne
    @JoinColumn(name = "voyageur_id")
    private Utilisateur voyageur;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pays_depart_id")
    private Pays paysDepart;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pays_destination_id")
    private Pays paysDestination;

    // Business Logic
    public boolean createAnnonceExpediteur(Utilisateur expediteur, Double poids, Double prix, Pays paysDepart, Pays paysDestination) {
        this.expediteur = expediteur;
        this.poids = poids;
        this.prix = prix;
        this.paysDepart = paysDepart;
        this.paysDestination = paysDestination;
        this.dateCreation = new Date();
        return true;
    }
}
