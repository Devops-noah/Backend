package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.Statut;
import lombok.Data;

import java.util.Date;

@Data
public class DemandeResponse {
    private Long id;
    private String expediteurEmail;
    private Statut status;
    private Date createdAt;
    private String voyageurNom;

}
