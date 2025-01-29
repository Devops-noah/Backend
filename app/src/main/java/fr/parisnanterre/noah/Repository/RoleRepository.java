package fr.parisnanterre.noah.Repository;

import fr.parisnanterre.noah.Entity.Role;
import fr.parisnanterre.noah.Entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleType name);  // Correct method to find a Role by its name
}
