package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Utilisateur;
import fr.parisnanterre.noah.Repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Optional<Utilisateur> getUtilisateurById(Integer id) {
        return utilisateurRepository.findById(id);
    }

    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur updateUtilisateur(Integer id, Utilisateur utilisateurDetails) {
        return utilisateurRepository.findById(id)
                .map(utilisateur -> {
                    utilisateur.setNom(utilisateurDetails.getNom());
                    utilisateur.setPrenom(utilisateurDetails.getPrenom());
                    utilisateur.setEmail(utilisateurDetails.getEmail());
                    utilisateur.setMotDePasse(utilisateurDetails.getMotDePasse());
                    utilisateur.setTelephone(utilisateurDetails.getTelephone());
                    utilisateur.setAdresse(utilisateurDetails.getAdresse());
                    //utilisateur.setRole(utilisateurDetails.getRole());
                    return utilisateurRepository.save(utilisateur);

                }).orElseThrow(() -> new RuntimeException("Utilisateur not found"));
    }

    public void deleteUtilisateur(Integer id) {
        utilisateurRepository.deleteById(id);
    }

    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }
}
