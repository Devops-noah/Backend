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
        private Date dateDepart;
        private Date dateArrivee;
        private String paysDepart;
        private String paysDestination;
        private Integer voyageId;
        private Integer voyageurId;
        private String voyageurNom;
        private String voyageurEmail;
        private boolean approved;
        private boolean suspended;

        // Constructor using only Annonce (when user details come from Annonce itself)
        public AnnonceResponse(Annonce annonce) {
                this.id = annonce.getId();
                this.poidsDisponible = annonce.getPoidsDisponible();
                this.datePublication = annonce.getDatePublication();
                this.dateDepart = annonce.getVoyage().getDateDepart();
                this.dateArrivee = annonce.getVoyage().getDateArrivee();
                this.paysDepart = annonce.getVoyage().getPaysDepart().getNom();
                this.paysDestination = annonce.getVoyage().getPaysDestination().getNom();
                this.voyageId = annonce.getVoyage().getId();

                // Set voyageur details from annonce
                if (annonce.getVoyageur() != null) {
                        this.voyageurId = Math.toIntExact(annonce.getVoyageur().getId());
                        this.voyageurNom = annonce.getVoyageur().getNom();
                        this.voyageurEmail = annonce.getVoyageur().getEmail();
                }

                this.approved = annonce.isApproved();
                this.suspended = annonce.isSuspended();
        }

        // Constructor for explicit Annonce and Utilisateur objects
        public AnnonceResponse(Annonce annonce, Utilisateur utilisateur) {
                this.id = annonce.getId();
                this.poidsDisponible = annonce.getPoidsDisponible();
                this.datePublication = annonce.getDatePublication();
                this.dateDepart = annonce.getVoyage().getDateDepart();
                this.dateArrivee = annonce.getVoyage().getDateArrivee();
                this.paysDepart = annonce.getVoyage().getPaysDepart().getNom();
                this.paysDestination = annonce.getVoyage().getPaysDestination().getNom();
                this.voyageId = annonce.getVoyage().getId();

                // Use the provided Utilisateur for voyageur details
                if (utilisateur != null) {
                        this.voyageurId = Math.toIntExact(utilisateur.getId());
                        this.voyageurNom = utilisateur.getNom();
                        this.voyageurEmail = utilisateur.getEmail();
                }

                this.approved = annonce.isApproved();
                this.suspended = annonce.isSuspended();
        }

        // Default constructor for frameworks
        public AnnonceResponse() {
        }
}
