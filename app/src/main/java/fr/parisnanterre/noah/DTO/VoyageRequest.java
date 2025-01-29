package fr.parisnanterre.noah.DTO;

import fr.parisnanterre.noah.Entity.Voyage;
import lombok.Data;

@Data
public class VoyageRequest {
    private Voyage voyage; // Voyage object containing dates and other properties
    private String paysDepart; // Name of the departure country
    private String paysDestination; // Name of the destination country
}
