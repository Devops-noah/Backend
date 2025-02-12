package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.Utilisateur;
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
        private boolean approved;
        private boolean suspended;

        // ✅ Constructor accepting Annonce and Utilisateur
        public AnnonceResponse(Annonce annonce) {
                this.id = annonce.getId();
                this.datePublication = annonce.getDatePublication();
                this.poidsDisponible = annonce.getPoidsDisponible();
                this.voyageId = annonce.getVoyage().getId();
                this.dateDepart = annonce.getVoyage().getDateDepart();
                this.dateArrivee = annonce.getVoyage().getDateArrivee();
                this.paysDepart = annonce.getVoyage().getPaysDepart().getNom();
                this.paysDestination = annonce.getVoyage().getPaysDestination().getNom();

                // Extract voyageur from the voyage
                if (annonce.getVoyage().getVoyageur() != null) {
                        this.voyageurId = Math.toIntExact(annonce.getVoyage().getVoyageur().getId());
                        this.voyageurNom = annonce.getVoyage().getVoyageur().getNom();
                        this.voyageurEmail = annonce.getVoyage().getVoyageur().getEmail();
                }
        }

        public AnnonceResponse() {
                // Default constructor for frameworks and manual mapping
        }


        public AnnonceResponse(Annonce savedAnnonce, Utilisateur utilisateur) {
        }
}
