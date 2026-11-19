package r5a08.finduser.ms_proximity_detection.detection;

import java.time.Instant;

/**
 * Position GPS d'un utilisateur à un instant donné.
 */
public record Position(double latitude, double longitude, Instant timestamp) {
}
