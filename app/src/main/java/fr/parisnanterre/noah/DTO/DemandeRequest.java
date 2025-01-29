package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.Statut;
import lombok.Data;

import java.util.Date;

@Data
public class DemandeRequest {
    private Long expediteurId;
    private String expediteurEmail;
    private String expediteurNom;
    private Statut status;
    private Date createdAt;
}
