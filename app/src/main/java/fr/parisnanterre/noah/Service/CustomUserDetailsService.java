package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Expediteur;
import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Entity.Voyageur;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private Utilisateur utilisateur;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch the utilisateur from the database using email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur not found with email: " + email));
        System.out.println("Utilisateur: " + utilisateur);

        // Convert the role to a GrantedAuthority
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(utilisateur.getRole().getName().name());

        return new CustomUserDetails(utilisateur, authority);  // return CustomUserDetails instead of User

    }

}
