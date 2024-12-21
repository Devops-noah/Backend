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
    @JsonIgnore // Prevent serialization to avoid recursion
    @ToString.Exclude // Avoid recursion in toString()
    private List<Annonce> annoncesAsDepart;

    @OneToMany(mappedBy = "paysDestination", cascade = CascadeType.REMOVE)
    @JsonIgnore // Prevent serialization to avoid recursion
    @ToString.Exclude // Avoid recursion in toString()
    private List<Annonce> annoncesAsDestination;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.REMOVE)
    @JsonIgnore // Prevent serialization to avoid recursion
    @ToString.Exclude // Avoid recursion in toString()
    private List<Voyage> voyages;
}

