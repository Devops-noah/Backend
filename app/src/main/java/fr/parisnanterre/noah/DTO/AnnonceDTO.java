package fr.parisnanterre.noah.DTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class AnnonceDTO {

    // Fields for annonce details
    private LocalDate datePublication;
    private Double poidsDisponible;
    private Date dateDepart;
    private Date dateArrivee;

    // Additional fields for relationships
    private Integer voyageId;
    private String paysDepartNom;
    private String paysDestinationNom;

    // Constructor for mapping data directly
    public AnnonceDTO(LocalDate datePublication, Double poidsDisponible, Date dateDepart, Date dateArrivee,
                      Integer voyageId, String paysDepartNom, String paysDestinationNom) {
        this.datePublication = datePublication;
        this.poidsDisponible = poidsDisponible;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.voyageId = voyageId;
        this.paysDepartNom = paysDepartNom;
        this.paysDestinationNom = paysDestinationNom;
    }

    // Empty constructor for frameworks like Jackson
    public AnnonceDTO() {
    }
}
