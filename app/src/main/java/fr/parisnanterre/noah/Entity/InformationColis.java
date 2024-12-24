package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "information_colis")
public class InformationColis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float poids;
    private String dimensions;
    private String nature;
    private String categorie;
    private Date datePriseEnCharge;
    private String plageHoraire;

    @ManyToOne
    @JoinColumn(name = "demande_id")
    private Demande demande;
}
