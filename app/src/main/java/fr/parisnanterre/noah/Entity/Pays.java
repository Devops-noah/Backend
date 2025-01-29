// Pays entity
package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonIgnore
    @ToString.Exclude
    private List<Voyage> voyagesDepart;

    @OneToMany(mappedBy = "paysDestination", cascade = CascadeType.REMOVE)
    @JsonIgnore
    @ToString.Exclude
    private List<Voyage> voyagesDestination;
}

