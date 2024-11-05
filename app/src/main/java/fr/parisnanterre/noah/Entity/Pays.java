// Pays entity
package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.*;

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
}


