package fr.parisnanterre.noah.Service;

import fr.parisnanterre.noah.Entity.Segment;
import fr.parisnanterre.noah.Entity.Annonce;
import fr.parisnanterre.noah.Repository.SegmentRepository;
import fr.parisnanterre.noah.Repository.AnnonceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DemandeTransfertService {

    private final AnnonceRepository annonceRepository;
    private final SegmentRepository segmentRepository;

    private static final Logger log = LoggerFactory.getLogger(DemandeTransfertService.class);

    @Autowired
    public DemandeTransfertService(AnnonceRepository annonceRepository, SegmentRepository segmentRepository) {
        this.annonceRepository = annonceRepository;
        this.segmentRepository = segmentRepository;
    }

    public List<List<Segment>> rechercherSegments(String paysDepart, String paysArrivee) {
        log.info("Recherche des segments entre {} et {}", paysDepart, paysArrivee);

        List<Annonce> annonces = annonceRepository.findActiveAnnoncesWithPays();
        log.info("Nombre d'annonces actives trouvées : {}", annonces.size());

        List<Segment> segments = convertirAnnoncesEnSegments(annonces);

        Map<String, List<Segment>> graph = new HashMap<>();
        for (Segment segment : segments) {
            graph.computeIfAbsent(segment.getPointDepart().toLowerCase(), k -> new ArrayList<>()).add(segment);
        }

        List<List<Segment>> allPaths = findAllPaths(graph, paysDepart.toLowerCase(), paysArrivee.toLowerCase());
        log.info("Nombre de chemins trouvés avant filtrage par dates : {}", allPaths.size());

        // Filtrer les chemins par respect des dates
        List<List<Segment>> validPaths = new ArrayList<>();
        for (List<Segment> path : allPaths) {
            if (isPathDatesValid(path)) {
                validPaths.add(path);
                // Log des détails de la chaîne valide
                log.info("Chemin valide trouvé :");
                for (Segment segment : path) {
                    log.info("Segment - Départ: {}, Arrivée: {}, Date départ: {}, Date arrivée: {}",
                            segment.getPointDepart(),
                            segment.getPointArrivee(),
                            segment.getDateDepart(),
                            segment.getDateArrivee());
                }
            }
        }

        log.info("Nombre de chemins valides après filtrage par dates : {}", validPaths.size());
        return validPaths;
    }


    private List<Segment> convertirAnnoncesEnSegments(List<Annonce> annonces) {
        List<Segment> segments = new ArrayList<>();
        for (Annonce annonce : annonces) {
            Segment segment = new Segment();
            segment.setPointDepart(annonce.getVoyage().getPaysDepart().getNom());
            segment.setPointArrivee(annonce.getVoyage().getPaysDestination().getNom());
            segment.setDateDepart(annonce.getVoyage().getDateDepart());
            segment.setDateArrivee(annonce.getVoyage().getDateArrivee());
            segment.setVoyageur(annonce.getVoyageur());
            segment.setAnnonce(annonce); // Associer l'annonce directement

            Segment savedSegment = segmentRepository.save(segment);

            // Log du segment après la sauvegarde
            log.info("Segment sauvegardé : Départ={}, Arrivée={}, DateDépart={}, DateArrivée={}",
                    savedSegment.getPointDepart(),
                    savedSegment.getPointArrivee(),
                    savedSegment.getDateDepart(),
                    savedSegment.getDateArrivee());

            segments.add(savedSegment);
        }
        log.info("Conversion de {} annonces en segments réussie", annonces.size());
        return segments;
    }


    private List<List<Segment>> findAllPaths(Map<String, List<Segment>> graph, String start, String end) {
        List<List<Segment>> allPaths = new ArrayList<>();
        findPathsDFS(graph, start, end, new ArrayList<>(), allPaths, new HashSet<>());
        return allPaths;
    }

    private void findPathsDFS(Map<String, List<Segment>> graph, String current, String end, List<Segment> currentPath, List<List<Segment>> allPaths, Set<String> visited) {
        if (current.equals(end)) {
            allPaths.add(new ArrayList<>(currentPath));
            log.debug("Chemin trouvé : {}", currentPath);
            return;
        }

        if (!graph.containsKey(current)) {
            return;
        }

        visited.add(current);

        for (Segment segment : graph.get(current)) {
            if (visited.contains(segment.getPointArrivee().toLowerCase())) {
                continue;
            }

            currentPath.add(segment);
            findPathsDFS(graph, segment.getPointArrivee().toLowerCase(), end, currentPath, allPaths, visited);
            currentPath.remove(currentPath.size() - 1);
        }

        visited.remove(current);
    }

    private boolean isPathDatesValid(List<Segment> path) {
        for (int i = 0; i < path.size() - 1; i++) {
            Segment current = path.get(i);
            Segment next = path.get(i + 1);

            if (!areDatesValid(current.getDateArrivee(), next.getDateDepart())) {
                log.debug("Dates non valides entre {} et {}", current, next);
                return false;
            }
        }
        return true;
    }

    private boolean areDatesValid(Date dateArrivee, Date dateDepartSuivant) {
        if (dateArrivee == null || dateDepartSuivant == null) {
            return false;
        }

        long differenceInMillis = dateDepartSuivant.getTime() - dateArrivee.getTime();
        long differenceInHours = differenceInMillis / (1000 * 60 * 60);

        return differenceInHours >= 4; // Minimum 4 heures d'écart
    }

    public void enregistrerChaine(List<Segment> segmentsChoisis) {
        log.info("Enregistrement de la chaîne de {} segments", segmentsChoisis.size());
        segmentRepository.saveAll(segmentsChoisis);
        log.info("Chaîne enregistrée avec succès");
    }
}
