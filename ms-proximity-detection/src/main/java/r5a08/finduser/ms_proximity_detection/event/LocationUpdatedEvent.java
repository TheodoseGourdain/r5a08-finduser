package r5a08.finduser.ms_proximity_detection.event;

import java.time.Instant;

import r5a08.finduser.ms_proximity_detection.detection.Position;

/**
 * Événement lu dans le topic user.location.updated (publié par MS-Location).
 * Copie locale du contrat : chaque service possède sa propre classe, seul le format JSON est partagé.
 *
 * Exemple :
 * {"userId": "123", "latitude": 47.2184, "longitude": -1.5536, "timestamp": "2026-01-10T10:15:30Z"}
 */
public record LocationUpdatedEvent(
        String userId,
        double latitude,
        double longitude,
        String timestamp
) {

    /**
     * Convertit l'événement en position exploitable pour la détection.
     * Lève une exception si l'événement est incomplet ou si le timestamp n'est pas au format ISO.
     */
    public Position toPosition() {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId manquant");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp manquant");
        }
        return new Position(latitude, longitude, Instant.parse(timestamp));
    }
}
