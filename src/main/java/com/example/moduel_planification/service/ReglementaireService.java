package com.example.moduel_planification.service;

import com.example.moduel_planification.entity.Affectation;
import com.example.moduel_planification.entity.Alerte;
import com.example.moduel_planification.entity.Controleur;
import com.example.moduel_planification.entity.Equipe;
import com.example.moduel_planification.entity.Secteur;
import com.example.moduel_planification.entity.Shift;
import com.example.moduel_planification.enums.ComplexiteSecteur;
import com.example.moduel_planification.enums.NiveauAlerte;
import com.example.moduel_planification.enums.RoleAffectation;
import com.example.moduel_planification.enums.SurveillanceLevel;
import com.example.moduel_planification.enums.TypeShift;
import com.example.moduel_planification.exception.ViolationReglementaireException;
import com.example.moduel_planification.repository.AffectationRepository;
import com.example.moduel_planification.repository.AlerteRepository;
import com.example.moduel_planification.repository.ControleurRepository;
import com.example.moduel_planification.repository.EquipeRepository;
import com.example.moduel_planification.repository.SecteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReglementaireService {

    private final AffectationRepository affectationRepository;
    private final AlerteRepository alerteRepository;
    private final ControleurRepository controleurRepository;
    private final EquipeRepository equipeRepository;
    private final SecteurRepository secteurRepository;

    @Autowired
    public ReglementaireService(AffectationRepository affectationRepository,
                                AlerteRepository alerteRepository,
                                ControleurRepository controleurRepository,
                                EquipeRepository equipeRepository,
                                SecteurRepository secteurRepository) {
        this.affectationRepository = affectationRepository;
        this.alerteRepository = alerteRepository;
        this.controleurRepository = controleurRepository;
        this.equipeRepository = equipeRepository;
        this.secteurRepository = secteurRepository;
    }

    // ─────────────────────────────────────────────────────────
    // RR-P1 : durée de vacation max 8h
    // ─────────────────────────────────────────────────────────
    public void validerDureeShift(Shift shift) {
        LocalTime debut = shift.getHeureDebut();
        LocalTime fin = shift.getHeureFin();
        if (debut == null || fin == null) return;

        long minutes = calculerDureeMinutes(debut, fin, shift.getTypeShift());

        if (minutes > 480) {
            throw new ViolationReglementaireException("RR_P1",
                    "RR-P1 : La durée du shift dépasse 8 heures (durée calculée : "
                    + minutes / 60 + "h" + minutes % 60 + "min). Maximum autorisé : 8h.");
        }
    }

    // ─────────────────────────────────────────────────────────
    // Validation globale d'une affectation (RR-P2, P3, P4, P5)
    // ─────────────────────────────────────────────────────────
    @Transactional
    public void validerAffectation(Affectation affectation) {
        Controleur controleur = controleurRepository
                .findById(affectation.getControleur().getId())
                .orElseThrow(() -> new ViolationReglementaireException("CONTROLEUR_INTROUVABLE",
                        "Contrôleur introuvable : " + affectation.getControleur().getId()));

        Secteur secteur = secteurRepository
                .findById(affectation.getSecteur().getIdSecteur())
                .orElseThrow(() -> new ViolationReglementaireException("SECTEUR_INTROUVABLE",
                        "Secteur introuvable : " + affectation.getSecteur().getIdSecteur()));

        Shift shift = secteur.getShift();
        if (shift == null) return;

        validerCompositionSecteur(affectation, secteur);
        validerUniciteSurShift(controleur, secteur, affectation.getIdAffectation());
        validerReposMinimum(controleur, shift);
        verifierNuitsConsecutives(controleur, shift);
        genererAlertesPause(controleur, shift);
        validerProfilVigilance(affectation, controleur, secteur, shift);
        validerAlertes30Jours(controleur, shift);
        verifierVigilanceModereeSecteur(affectation, controleur, secteur);
    }

    // ─────────────────────────────────────────────────────────
    // RM-P1 : taille maximale d'une équipe
    // ─────────────────────────────────────────────────────────
    public void validerTailleEquipe(Controleur controleur, boolean isUpdate) {
        if (controleur.getEquipe() == null) return;

        Equipe equipe = equipeRepository.findById(controleur.getEquipe().getIdEquipe())
                .orElseThrow(() -> new ViolationReglementaireException("EQUIPE_INTROUVABLE",
                        "Équipe introuvable : " + controleur.getEquipe().getIdEquipe()));

        if (equipe.getMaxMembres() == null) return;

        long count = isUpdate
                ? controleurRepository.countByEquipeExcluding(equipe.getIdEquipe(), controleur.getId())
                : controleurRepository.countByEquipe(equipe.getIdEquipe());

        if (count >= equipe.getMaxMembres()) {
            throw new ViolationReglementaireException("RM_P1",
                    "RM-P1 : L'équipe \"" + equipe.getNomEquipe() + "\" a atteint sa capacité maximale de "
                    + equipe.getMaxMembres() + " membres (actuellement " + count + ").");
        }
    }

    // ─────────────────────────────────────────────────────────
    // RM-P2 : composition obligatoire du secteur (max 2 affectations)
    // ─────────────────────────────────────────────────────────
    private void validerCompositionSecteur(Affectation affectation, Secteur secteur) {
        List<Affectation> existantes = affectationRepository.findBySecteur(secteur.getIdSecteur());

        // En cas de mise à jour, exclure l'affectation courante du compte
        long count = existantes.stream()
                .filter(a -> affectation.getIdAffectation() == null
                        || !a.getIdAffectation().equals(affectation.getIdAffectation()))
                .count();

        if (count >= 2) {
            throw new ViolationReglementaireException("RM_P2",
                    "RM-P2 : Le secteur " + secteur.getIdSecteur()
                    + " a déjà atteint sa composition maximale (2 contrôleurs : 1 exécutant + 1 assistant).");
        }
    }

    // ─────────────────────────────────────────────────────────
    // RM-P3 : unicité d'affectation par shift
    // ─────────────────────────────────────────────────────────
    private void validerUniciteSurShift(Controleur controleur, Secteur secteurCible, Integer affectationId) {
        if (secteurCible.getShift() == null) return;

        Integer shiftCibleId = secteurCible.getShift().getIdShift();

        List<Affectation> historique = affectationRepository.findByControleurIdWithShift(controleur.getId());

        boolean dejaAffecte = historique.stream()
                .filter(a -> affectationId == null || !a.getIdAffectation().equals(affectationId))
                .anyMatch(a -> {
                    Shift s = a.getSecteur().getShift();
                    return s != null && s.getIdShift().equals(shiftCibleId);
                });

        if (dejaAffecte) {
            throw new ViolationReglementaireException("RM_P3",
                    "RM-P3 : Le contrôleur " + controleur.getName()
                    + " est déjà affecté à un secteur sur ce shift. Un contrôleur ne peut être affecté"
                    + " qu'à un seul secteur par shift.");
        }
    }

    // ─────────────────────────────────────────────────────────
    // RR-P2 : repos minimum 8h entre deux shifts
    // ─────────────────────────────────────────────────────────
    private void validerReposMinimum(Controleur controleur, Shift newShift) {
        if (newShift.getDate() == null || newShift.getHeureDebut() == null) return;

        LocalDateTime newShiftStart = LocalDateTime.of(newShift.getDate(), newShift.getHeureDebut());

        List<Affectation> historique = affectationRepository.findByControleurIdWithShift(controleur.getId());

        LocalDateTime dernierShiftFin = historique.stream()
                .map(a -> a.getSecteur().getShift())
                .filter(s -> s != null && s.getDate() != null && s.getHeureFin() != null)
                .filter(s -> !s.getIdShift().equals(newShift.getIdShift()))
                .map(this::getShiftEndDateTime)
                .filter(dt -> dt.isBefore(newShiftStart))
                .max(Comparator.naturalOrder())
                .orElse(null);

        if (dernierShiftFin != null) {
            long minutesRepos = ChronoUnit.MINUTES.between(dernierShiftFin, newShiftStart);
            if (minutesRepos < 480) {
                long h = minutesRepos / 60;
                long m = minutesRepos % 60;
                throw new ViolationReglementaireException("RR_P2",
                        "RR-P2 : Repos insuffisant — " + h + "h" + m + "min depuis le dernier shift. "
                        + "Minimum requis : 8h. Prochain shift autorisé à partir de "
                        + dernierShiftFin.plusHours(8).toLocalTime() + " le "
                        + dernierShiftFin.plusHours(8).toLocalDate() + ".");
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // RR-P3 : 2 nuits consécutives → alerte ALERTE
    // RR-P4 : 3 nuits consécutives → alerte CRITIQUE + blocage
    // ─────────────────────────────────────────────────────────
    private void verifierNuitsConsecutives(Controleur controleur, Shift newShift) {
        if (newShift.getTypeShift() != TypeShift.NUIT || newShift.getDate() == null) return;

        List<Affectation> historique = affectationRepository.findByControleurIdWithShift(controleur.getId());

        List<LocalDate> nuitsPassees = historique.stream()
                .map(a -> a.getSecteur().getShift())
                .filter(s -> s != null && s.getTypeShift() == TypeShift.NUIT && s.getDate() != null)
                .filter(s -> !s.getIdShift().equals(newShift.getIdShift()))
                .map(Shift::getDate)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // Compter les nuits consécutives immédiatement avant le nouveau shift
        int consecutives = 0;
        LocalDate attendue = newShift.getDate().minusDays(1);
        for (LocalDate date : nuitsPassees) {
            if (date.equals(attendue)) {
                consecutives++;
                attendue = attendue.minusDays(1);
            } else if (date.isBefore(attendue)) {
                break;
            }
        }

        int total = consecutives + 1; // +1 pour le nouveau shift

        if (total >= 3) {
            Alerte alerte = new Alerte(null, "RR_P4_NUITS_CONSECUTIVES", NiveauAlerte.CRITIQUE,
                    null, controleur, controleur.getEquipe());
            alerteRepository.save(alerte);
            throw new ViolationReglementaireException("RR_P4",
                    "RR-P4 : Blocage — " + total + " nuits consécutives détectées pour "
                    + controleur.getName() + ". Un repos de 48h est obligatoire avant "
                    + "toute nouvelle affectation de nuit.");
        }

        if (total == 2) {
            Alerte alerte = new Alerte(null, "RR_P3_NUITS_CONSECUTIVES", NiveauAlerte.ALERTE,
                    null, controleur, controleur.getEquipe());
            alerteRepository.save(alerte);
            // Avertissement uniquement, pas de blocage
        }
    }

    // ─────────────────────────────────────────────────────────
    // RR-P5 : shift > 2h → alerte INFO pause obligatoire
    // ─────────────────────────────────────────────────────────
    private void genererAlertesPause(Controleur controleur, Shift shift) {
        if (shift.getHeureDebut() == null || shift.getHeureFin() == null) return;

        long minutes = calculerDureeMinutes(shift.getHeureDebut(), shift.getHeureFin(), shift.getTypeShift());
        if (minutes > 120) {
            Alerte alerte = new Alerte(null, "RR_P5_PAUSE_OBLIGATOIRE", NiveauAlerte.INFO,
                    null, controleur, null);
            alerteRepository.save(alerte);
        }
    }

    // ─────────────────────────────────────────────────────────
    // RP-P1 : vigilance FAIBLE → interdictions d'affectation
    // ─────────────────────────────────────────────────────────
    private void validerProfilVigilance(Affectation affectation, Controleur controleur,
                                        Secteur secteur, Shift shift) {
        if (controleur.getSurveillance() != SurveillanceLevel.LOW) return;

        if (secteur.getComplexite() == ComplexiteSecteur.FORTE) {
            throw new ViolationReglementaireException("RP_P1_COMPLEXITE",
                    "RP-P1 : Le contrôleur " + controleur.getName()
                    + " a un niveau de vigilance FAIBLE et ne peut pas être affecté"
                    + " à un secteur de forte complexité.");
        }

        if (affectation.getRole() == RoleAffectation.CHEF) {
            throw new ViolationReglementaireException("RP_P1_ROLE",
                    "RP-P1 : Le contrôleur " + controleur.getName()
                    + " a un niveau de vigilance FAIBLE et ne peut pas occuper"
                    + " un poste exécutant (Chef).");
        }

        if (shift.getTypeShift() == TypeShift.NUIT) {
            throw new ViolationReglementaireException("RP_P1_NUIT",
                    "RP-P1 : Le contrôleur " + controleur.getName()
                    + " a un niveau de vigilance FAIBLE et ne peut pas être affecté"
                    + " à un shift de nuit.");
        }
    }

    // ─────────────────────────────────────────────────────────
    // RP-P2 : > 3 alertes sur 30 jours → interdit shift de nuit
    // ─────────────────────────────────────────────────────────
    private void validerAlertes30Jours(Controleur controleur, Shift shift) {
        if (shift.getTypeShift() != TypeShift.NUIT) return;

        LocalDateTime since = LocalDateTime.now().minusDays(30);
        long nbAlertes = alerteRepository.countByControleurSince(controleur.getId(), since);

        if (nbAlertes > 3) {
            throw new ViolationReglementaireException("RP_P2",
                    "RP-P2 : Le contrôleur " + controleur.getName()
                    + " a " + nbAlertes + " alertes sur les 30 derniers jours (max autorisé : 3)."
                    + " Affectation à un shift de nuit interdite.");
        }
    }

    // ─────────────────────────────────────────────────────────
    // RP-P3 : deux membres MODERE sur le même secteur → alerte
    // ─────────────────────────────────────────────────────────
    private void verifierVigilanceModereeSecteur(Affectation affectation, Controleur controleur, Secteur secteur) {
        if (controleur.getSurveillance() != SurveillanceLevel.MEDIUM) return;

        List<Affectation> existantes = affectationRepository.findBySecteur(secteur.getIdSecteur());

        Optional<Affectation> autreMembre = existantes.stream()
                .filter(a -> affectation.getIdAffectation() == null
                        || !a.getIdAffectation().equals(affectation.getIdAffectation()))
                .filter(a -> a.getControleur() != null
                        && a.getControleur().getSurveillance() == SurveillanceLevel.MEDIUM)
                .findFirst();

        if (autreMembre.isPresent()) {
            Alerte alerte = new Alerte(null, "RP_P3_VIGILANCE_MODEREE_SECTEUR", NiveauAlerte.ALERTE,
                    null, null, controleur.getEquipe());
            alerteRepository.save(alerte);
        }
    }

    // ─────────────────────────────────────────────────────────
    // Utilitaires
    // ─────────────────────────────────────────────────────────
    private long calculerDureeMinutes(LocalTime debut, LocalTime fin, TypeShift type) {
        if (fin.isAfter(debut)) {
            return ChronoUnit.MINUTES.between(debut, fin);
        }
        // Shift qui passe minuit (NUIT : 22:00 → 06:00)
        return ChronoUnit.MINUTES.between(debut, LocalTime.MAX)
                + ChronoUnit.MINUTES.between(LocalTime.MIDNIGHT, fin) + 1;
    }

    private LocalDateTime getShiftEndDateTime(Shift shift) {
        LocalTime fin = shift.getHeureFin();
        LocalDate date = shift.getDate();
        // Pour NUIT, heureFin (ex: 06:00) est le lendemain
        if (shift.getTypeShift() == TypeShift.NUIT && fin != null && fin.getHour() < 12) {
            return LocalDateTime.of(date.plusDays(1), fin);
        }
        return LocalDateTime.of(date, fin);
    }
}