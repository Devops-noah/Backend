package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.Voyage;
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

    public VoyageResponse(int id, Date dateDepart, Date dateArrivee, int paysDepartId, String paysDepartNom, int paysDestinationId, String paysDestinationNom, int voyageurId, String voyageurNom, String voyageurEmail, List<AnnonceResponse> annonces) {
        this.id = id;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.paysDepartId = paysDepartId;
        this.paysDepartNom = paysDepartNom;
        this.paysDestinationId = paysDestinationId;
        this.paysDestinationNom = paysDestinationNom;
        this.voyageurId = voyageurId;
        this.voyageurNom = voyageurNom;
        this.voyageurEmail = voyageurEmail;
        this.annonces = annonces;
    }

    public VoyageResponse(Voyage savedVoyage) {
        this.id = Math.toIntExact(savedVoyage.getId());
        this.dateDepart = savedVoyage.getDateDepart();
        this.dateArrivee = savedVoyage.getDateArrivee();

        // Set Pays Information
        if (savedVoyage.getPaysDepart() != null) {
            this.paysDepartId = Math.toIntExact(savedVoyage.getPaysDepart().getId());
            this.paysDepartNom = savedVoyage.getPaysDepart().getNom();
        }

        if (savedVoyage.getPaysDestination() != null) {
            this.paysDestinationId = Math.toIntExact(savedVoyage.getPaysDestination().getId());
            this.paysDestinationNom = savedVoyage.getPaysDestination().getNom();
        }

        // Set Voyageur Information
        if (savedVoyage.getVoyageur() != null) {
            this.voyageurId = Math.toIntExact(savedVoyage.getVoyageur().getId());
            this.voyageurNom = savedVoyage.getVoyageur().getNom();
            this.voyageurEmail = savedVoyage.getVoyageur().getEmail();
        }

        // Convert Annonce list if necessary
        if (savedVoyage.getAnnonces() != null) {
            this.annonces = savedVoyage.getAnnonces().stream()
                    .map(AnnonceResponse::new)
                    .toList();
        }
        System.out.println("Voyage annonces: " + savedVoyage.getAnnonces());

    }


    public VoyageResponse() {
        
    }

}
