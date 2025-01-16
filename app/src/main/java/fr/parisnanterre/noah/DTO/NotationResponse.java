package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.StatutNotation;
import lombok.Data;

@Data
public class NotationResponse {
    private Long id;
    private Long utilisateurId;
    private String userName;
    private String userFirstName;
    private int note;
    private String commentaire;
    private String datePublication; // Date en format String (jour/mois/année)
    private StatutNotation status = StatutNotation.PENDING;; // Add status field
}
