package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @ManyToOne
    @JoinColumn(name = "pays_depart_id")
    @JsonManagedReference("paysDepartAnnonceReference")
    private Pays paysDepart;

    @ManyToOne
    @JoinColumn(name = "pays_destination_id")
    @JsonManagedReference("paysDestinationAnnonceReference")
    private Pays paysDestination;

    @ManyToOne(fetch = FetchType.EAGER) // or FetchType.LAZY if you handle the fetch explicitly in the query
    @JoinColumn(name = "voyage_id", referencedColumnName = "id") // Ensure this matches your actual column name
    private Voyage voyage;

    // Business Logic
    public boolean createAnnonceExpediteur(Utilisateur expediteur, Double poids, Double prix, Pays paysDepart, Pays paysDestination, Voyage voyage) {
        this.expediteur = expediteur;
        this.poids = poids;
        this.prix = prix;
        this.paysDepart = paysDepart;
        this.paysDestination = paysDestination;
        this.dateCreation = new Date();
        this.voyage = voyage;
        return true;
    }


}
