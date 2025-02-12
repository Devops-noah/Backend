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
        // If the user has no type, return a default or null
        if (utilisateur.getUserTypes() == null) {
            return null;  // or return a default value like "user"
        }

        if (utilisateur instanceof Voyageur) {
            return "voyageur";
        } else if (utilisateur instanceof Expediteur) {
            return "expediteur";
        } else if (utilisateur instanceof AdminType) {
            return "admin";
        }

        return null;  // or throw an exception if neither is found
    }

    public Long getId() {
        return utilisateur.getId(); // Supposons que votre objet Utilisateur a un champ id
    }

    @JsonIgnore
    public byte[] getProfileImage() {
        return utilisateur.getProfileImage() != null ? utilisateur.getProfileImage().getBytes() : new byte[0];
    }


}
