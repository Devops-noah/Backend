package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Entity
@Data
public class Voyage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Date dateDepart;
    private Date dateArrivee;

    @OneToMany(mappedBy = "voyage")
    @JsonManagedReference // Forward serialization for Voyage -> Annonce
    @ToString.Exclude // Avoid recursion in toString()
    private List<Annonce> annonces;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    @ToString.Exclude // Prevent recursive `toString()`
    private Pays destination;

    @ManyToOne
    @JoinColumn(name = "voyageur_id")
    @ToString.Exclude
    private Utilisateur voyageur;
}