package fr.parisnanterre.noah.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class AnnonceResponseDto {
        private Long id;
        private Date dateDepart;
        private Date dateArrivee;
        private Date datePublication;
        private Double poidsDisponible;
        private String paysDepart;       // Only the name
        private String paysDestination; // Only the name
        private String voyageId;       // Voyage name or other attribute

}
