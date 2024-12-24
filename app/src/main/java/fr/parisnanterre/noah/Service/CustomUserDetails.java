package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Expediteur;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyageur;
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

    public String getUserType() {
        if (utilisateur instanceof Voyageur) {
            return "voyageur";
        } else if (utilisateur instanceof Expediteur) {
            return "expediteur";
        }
        return null; // or throw an exception if neither is found
    }
}
