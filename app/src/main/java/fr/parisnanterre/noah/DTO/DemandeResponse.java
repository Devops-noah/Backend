package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.Statut;
import lombok.Data;

import java.util.Date;

@Data
public class DemandeResponse {
    private Long id;
    private Long expediteurId;
    private String expediteurEmail;
    private String expediteurNom;
    private Statut status;
    private Date createdAt;
    private String voyageurNom;
    private InformationColisResponse informationColis;

    public DemandeResponse(Long id, Long expediteurId, String expediteurEmail, String expediteurNom, Statut status, Date createdAt, String voyageurNom, InformationColisResponse informationColis) {
        this.id = id;
        this.expediteurId = expediteurId;
        this.expediteurEmail = expediteurEmail;
        this.expediteurNom = expediteurNom;
        this.status = status;
        this.createdAt = createdAt;
        this.voyageurNom = voyageurNom;
        this.informationColis = informationColis;
    }
    public DemandeResponse(){}
}
