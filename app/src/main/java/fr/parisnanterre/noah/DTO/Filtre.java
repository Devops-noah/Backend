package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Entity.Pays;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filtre {
    private Date dateDepart;
    private Date dateArrivee;
    private String paysDepart;
    private String paysDestination;

}
