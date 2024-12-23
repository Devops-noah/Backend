package fr.parisnanterre.noah.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Date datePublication;

    @NotNull
    private Double poidsDisponible;


    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore // Prevent serialization of voyageur to avoid recursion
    @ToString.Exclude // Avoid recursion in toString()
    private Voyageur voyageur;

    //@NotNull
    @OneToMany(mappedBy = "annonce", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Demande> demandes;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER) // or FetchType.LAZY if you handle the fetch explicitly in the query
    @JoinColumn(name = "voyage_id", referencedColumnName = "id") // Ensure this matches your actual column name
    @JsonIgnore // Prevent recursion for Voyage -> Annonce
    @ToString.Exclude // Avoid recursion in toString()
    private Voyage voyage;

}