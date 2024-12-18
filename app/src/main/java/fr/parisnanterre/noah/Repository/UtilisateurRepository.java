// Utilisateur repository

package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

    Boolean existsByEmail(String email);
}


