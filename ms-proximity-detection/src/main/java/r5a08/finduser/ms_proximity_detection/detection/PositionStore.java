package r5a08.finduser.ms_proximity_detection.detection;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.stereotype.Component;

/**
 * Mémoire des dernières positions connues, une par utilisateur.
 * Les données sont perdues au redémarrage du service (limite à citer dans le README).
 */
@Component
public class PositionStore {

    private final Map<String, Position> lastPositions = new ConcurrentHashMap<>();

    /**
     * Enregistre la position si elle est plus récente que celle déjà connue.
     *
     * @return true si la position a été enregistrée, false si elle est périmée (ou en double)
     */
    public boolean updateIfNewer(String userId, Position position) {
        AtomicBoolean updated = new AtomicBoolean(false);

        // compute() lit et remplace la valeur en une seule opération atomique
        lastPositions.compute(userId, (id, current) -> {
            if (current == null || position.timestamp().isAfter(current.timestamp())) {
                updated.set(true);
                return position;
            }
            return current;
        });

        return updated.get();
    }

    /** Dernière position connue d'un utilisateur, si on en a reçu une. */
    public Optional<Position> find(String userId) {
        return Optional.ofNullable(lastPositions.get(userId));
    }
}
