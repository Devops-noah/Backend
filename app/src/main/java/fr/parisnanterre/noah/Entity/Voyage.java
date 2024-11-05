// Voyage entity
package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voyage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dateDepart;

    @Temporal(TemporalType.DATE)
    private Date dateArrivee;

    private Double poidsDisponible;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "destination_id")
    private Pays destination;

    @ManyToOne
    @JoinColumn(name="voyageur_id")
    private Utilisateur voyageur;

    // Business Logic
    public boolean creerVoyage(Date dateDepart, Date dateArrivee, Double poidsDisponible, Pays destination, Utilisateur voyageur) {
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.poidsDisponible = poidsDisponible;
        this.destination = destination;
        this.voyageur = voyageur;
        return true;
    }
}
