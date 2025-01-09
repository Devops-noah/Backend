package fr.parisnanterre.noah.DTO;

import lombok.Data;

@Data
public class NotationResponse {
    private Long utilisateurId;
    private String userName;
    private String userFirstName;
    private int note;
    private String commentaire;
    private String datePublication; // Date en format String (jour/mois/année)
}
