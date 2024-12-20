package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Livraison;
import fr.parisnanterre.noah.Repository.LivraisonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;

    public Livraison getLivraisonById(int id) {
        return livraisonRepository.findById(id).orElseThrow(() -> new RuntimeException("Livraison not found"));
    }

    public List<Livraison> getAllLivraisons() {
        return livraisonRepository.findAll();
    }
}

