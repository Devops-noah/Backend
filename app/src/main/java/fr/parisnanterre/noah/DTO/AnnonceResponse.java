package fr.parisnanterre.noah.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class AnnonceResponse {
        private Long id;
        private Double poidsDisponible;
        private Date datePublication;
        private Date dateDepart; // Retrieved via the linked Voyage
        private Date dateArrivee; // Retrieved via the linked Voyage
        private String paysDepart;       // Retrieved via the linked Voyage
        private String paysDestination; // Retrieved via the linked Voyage
        private Integer voyageId;          // The ID of the linked Voyage
        private Integer voyageurId;
        private String voyageurNom;
        private String voyageurEmail;

}
