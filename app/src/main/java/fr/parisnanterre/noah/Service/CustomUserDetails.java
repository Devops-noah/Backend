package fr.parisnanterre.noah.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.parisnanterre.noah.Entity.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

public class CustomUserDetails extends User implements UserDetails {

    private final Utilisateur utilisateur;

    public CustomUserDetails(Utilisateur utilisateur, SimpleGrantedAuthority authority) {
        super(utilisateur.getEmail(), utilisateur.getMotDePasse(), Collections.singletonList(authority));
        this.utilisateur = utilisateur;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public RoleType getRole() {
        return utilisateur.getRole() != null ? utilisateur.getRole().getName() : null;
    }

    public String getUserType() {
        // If userTypes is empty but the user is an ADMIN, return "admin"
        if ((utilisateur.getUserTypes() == null || utilisateur.getUserTypes().isEmpty())
                && utilisateur.getRole().getName() == RoleType.ROLE_ADMIN) {
            return "admin";
        }

        // Convert Set<UserType> to a comma-separated string
        return utilisateur.getUserTypes().stream()
                .map(Enum::name)
                .map(String::toLowerCase)
                .reduce((a, b) -> a + "," + b)
                .orElse(null);
    }


    public Long getId() {
        return utilisateur.getId(); // Supposons que votre objet Utilisateur a un champ id
    }

    @JsonIgnore
    public byte[] getProfileImage() {
        return utilisateur.getProfileImage() != null ? utilisateur.getProfileImage().getBytes() : new byte[0];
    }


}
