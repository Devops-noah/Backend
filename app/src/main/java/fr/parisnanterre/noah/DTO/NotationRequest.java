package fr.parisnanterre.noah.DTO;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class NotationRequest {
    private Long utilisateurId;

    private int note;

    private String commentaire;

    private String datePublication;
}
