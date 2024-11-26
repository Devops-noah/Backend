// Pays entity
package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pays {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String codeISO;

    private String ville;

    @OneToMany(mappedBy = "paysDepart", cascade = CascadeType.REMOVE)
    @JsonBackReference("paysDepartAnnonceReference")
    private List<Annonce> annoncesAsDepart;

    @OneToMany(mappedBy = "paysDestination", cascade = CascadeType.REMOVE)
    @JsonBackReference("paysDestinationAnnonceReference")
    private List<Annonce> annoncesAsDestination;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.REMOVE)
    @JsonBackReference("paysVoyageReference")
    private List<Voyage> voyages;
}


