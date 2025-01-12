package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.DTO.AnnonceResponse;
import fr.parisnanterre.noah.DTO.UtilisateurProfileResponse;
import fr.parisnanterre.noah.Entity.*;
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

    public UtilisateurProfileResponse getUserProfile(String email) {
        // Récupérer l'utilisateur par email
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        // Créer un objet de réponse
        UtilisateurProfileResponse profile = new UtilisateurProfileResponse();

        // Remplir les informations communes pour tous les utilisateurs
        profile.setId(utilisateur.getId());
        profile.setNom(utilisateur.getNom());
        profile.setPrenom(utilisateur.getPrenom());
        profile.setEmail(utilisateur.getEmail());
        profile.setTelephone(utilisateur.getTelephone());
        profile.setAdresse(utilisateur.getAdresse());

        // Récupérer le nombre de notifications non lues pour l'utilisateur
        int notificationCount = utilisateur.getNotificationCount();  // Le compteur de notifications non lues
        profile.setNotificationCount(notificationCount);

        // Déterminer le type d'utilisateur et remplir les données correspondantes
        if (utilisateur instanceof Voyageur voyageur) {
            profile.setType("voyageur");

            // Mapper les annonces pour le voyageur
            List<AnnonceResponse> annonces = voyageur.getAnnonces().stream().map(annonce -> {
                AnnonceResponse annonceResponse = new AnnonceResponse();
                annonceResponse.setId(annonce.getId());
                annonceResponse.setDatePublication(annonce.getDatePublication());
                annonceResponse.setPoidsDisponible(annonce.getPoidsDisponible());

                if (annonce.getVoyage() != null) {
                    annonceResponse.setDateDepart(annonce.getVoyage().getDateDepart());
                    annonceResponse.setDateArrivee(annonce.getVoyage().getDateArrivee());
                    annonceResponse.setPaysDepart(annonce.getVoyage().getPaysDepart() != null ? annonce.getVoyage().getPaysDepart().getNom() : null);
                    annonceResponse.setPaysDestination(annonce.getVoyage().getPaysDestination() != null ? annonce.getVoyage().getPaysDestination().getNom() : null);
                    annonceResponse.setVoyageId(annonce.getVoyage().getId());
                }
                return annonceResponse;
            }).toList();
            profile.setAnnonces(annonces);

            // Mapper les voyages pour le voyageur
            List<Voyage> voyages = voyageur.getVoyages().stream().map(voyage -> {
                Voyage voyageResponse = new Voyage();
                voyageResponse.setId(voyage.getId());
                voyageResponse.setDateDepart(voyage.getDateDepart());
                voyageResponse.setDateArrivee(voyage.getDateArrivee());
                voyageResponse.setPaysDepart(voyage.getPaysDepart() != null ? voyage.getPaysDepart() : null);
                voyageResponse.setPaysDestination(voyage.getPaysDestination() != null ? voyage.getPaysDestination() : null);
                return voyageResponse;
            }).toList();
            profile.setVoyages(voyages);

        } else if (utilisateur instanceof Expediteur) {
            profile.setType("expediteur");
            profile.setMessage("L'utilisateur est un expediteur. Les colis ne sont pas disponibles pour le moment.");
        } else if (utilisateur instanceof AdminType) {
            profile.setType("admin");
        }

        return profile;
    }



//    public Optional<Utilisateur> getUtilisateurById(Integer id) {
//        return utilisateurRepository.findById(id);
//    }

//    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
//        return utilisateurRepository.save(utilisateur);
//    }
//
//    public Utilisateur updateUtilisateur(Integer id, Utilisateur utilisateurDetails) {
//        return utilisateurRepository.findById(id)
//                .map(utilisateur -> {
//                    utilisateur.setNom(utilisateurDetails.getNom());
//                    utilisateur.setPrenom(utilisateurDetails.getPrenom());
//                    utilisateur.setEmail(utilisateurDetails.getEmail());
//                    utilisateur.setMotDePasse(utilisateurDetails.getMotDePasse());
//                    utilisateur.setTelephone(utilisateurDetails.getTelephone());
//                    utilisateur.setAdresse(utilisateurDetails.getAdresse());
//                    //utilisateur.setRole(utilisateurDetails.getRole());
//                    return utilisateurRepository.save(utilisateur);
//
//                }).orElseThrow(() -> new RuntimeException("Utilisateur not found"));
//    }
//
//    public void deleteUtilisateur(Integer id) {
//        utilisateurRepository.deleteById(id);
//    }
//
//    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
//        return utilisateurRepository.findByEmail(email);
//    }
}
