package r5a08.finduser.ms_proximity_detection.detection;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import r5a08.finduser.ms_proximity_detection.event.ProximityViolationEvent;
import r5a08.finduser.ms_proximity_detection.event.ViolationEventPublisher;
import r5a08.finduser.ms_proximity_detection.rules.ProximityRulesService;

/**
 * Logique métier : à chaque nouvelle position, vérifie les règles d'éloignement
 * de l'utilisateur et publie une violation si une distance n'est pas respectée.
 */
@Service
public class ProximityDetectionService {

    private static final Logger log = LoggerFactory.getLogger(ProximityDetectionService.class);

    private final PositionStore positionStore;
    private final ProximityRulesService rulesService;
    private final DistanceCalculator distanceCalculator;
    private final ViolationEventPublisher violationPublisher;

    public ProximityDetectionService(PositionStore positionStore,
                                     ProximityRulesService rulesService,
                                     DistanceCalculator distanceCalculator,
                                     ViolationEventPublisher violationPublisher) {
        this.positionStore = positionStore;
        this.rulesService = rulesService;
        this.distanceCalculator = distanceCalculator;
        this.violationPublisher = violationPublisher;
    }

    public void handleLocationUpdate(String userId, Position position) {
        // 1. Mémoriser la position, sauf si elle est plus ancienne que celle déjà connue
        if (!positionStore.updateIfNewer(userId, position)) {
            log.info("Position périmée ignorée pour l'utilisateur {}", userId);
            return;
        }

        // 2. Utilisateurs dont celui-ci doit rester éloigné
        Set<String> restrictedUsers = rulesService.getRestrictedUsers(userId);

        // 3. Vérifier chaque règle
        for (String otherUserId : restrictedUsers) {
            checkRule(userId, position, otherUserId);
        }
    }

    private void checkRule(String userId, Position position, String otherUserId) {
        Optional<Position> otherPosition = positionStore.find(otherUserId);
        if (otherPosition.isEmpty()) {
            log.debug("Position inconnue pour {} : règle {} / {} non vérifiable", otherUserId, userId, otherUserId);
            return;
        }

        Double threshold = rulesService.getThreshold(userId, otherUserId);
        if (threshold == null) {
            log.warn("Règle incomplète dans Redis : aucun seuil pour {} / {}", userId, otherUserId);
            return;
        }

        double distance = distanceCalculator.distanceInMeters(position, otherPosition.get());
        log.info("Distance {} / {} : {} m (seuil {} m)", userId, otherUserId, Math.round(distance), threshold);

        // 4. Violation si la distance est strictement inférieure au seuil
        if (distance < threshold) {
            ProximityViolationEvent violation = new ProximityViolationEvent(
                    userId,
                    otherUserId,
                    Math.round(distance),
                    threshold,
                    Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
            );
            violationPublisher.publishViolation(violation);
        }
    }
}
