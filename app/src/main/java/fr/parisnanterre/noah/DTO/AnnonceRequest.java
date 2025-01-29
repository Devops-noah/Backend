package fr.parisnanterre.noah.DTO;
import lombok.Data;

import java.util.Date;

@Data
public class AnnonceRequest {
    private Date datePublication;
    private Double poidsDisponible;
    private Integer voyageId;
}
