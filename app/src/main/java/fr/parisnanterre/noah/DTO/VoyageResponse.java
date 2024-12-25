package fr.parisnanterre.noah.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VoyageResponse {
    private int id;
    private Date dateDepart;
    private Date dateArrivee;

    // Include necessary attributes of Pays directly
    private int paysDepartId;
    private String paysDepartNom;

    private int paysDestinationId;
    private String paysDestinationNom;

    // Include necessary attributes of Voyageur directly
    private int voyageurId;
    private String voyageurNom;
    private String voyageurEmail;

    // Annonce list
    private List<AnnonceResponse> annonces;
}
