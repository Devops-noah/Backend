package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.Annonce;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceRequest {
    private Double poids;
    private Double prix;
    private Long voyageId;
    private String paysDepartNom;
    private String paysDestinationNom;

    // Convert this DTO to an Annonce entity
    public Annonce toAnnonce() {
        Annonce annonce = new Annonce();
        annonce.setPoids(this.poids);
        annonce.setPrix(this.prix);
        return annonce;
    }

}
