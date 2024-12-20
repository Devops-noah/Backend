package fr.parisnanterre.noah.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)  // Store the enum value as a string in the DB
    private RoleType name;  // RoleType enum instead of String



}
