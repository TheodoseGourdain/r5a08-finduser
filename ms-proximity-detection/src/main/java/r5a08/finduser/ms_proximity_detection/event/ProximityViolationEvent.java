package r5a08.finduser.ms_proximity_detection.event;

/**
 * Événement publié dans le topic proximity.violation.detected (consommé par MS-Notification).
 * Contrat commun avec MS-Notification : ne pas modifier sans se concerter.
 *
 * Exemple :
 * {"userId": "123", "otherUserId": "456", "distance": 85, "threshold": 100.0, "timestamp": "2026-01-10T10:15:31Z"}
 *
 * @param userId      utilisateur qui vient de se déplacer
 * @param otherUserId utilisateur dont il doit rester éloigné
 * @param distance    distance mesurée, en mètres, arrondie au mètre
 * @param threshold   distance minimale à respecter, en mètres (lue dans Redis)
 * @param timestamp   instant de la détection, au format ISO en UTC
 */
public record ProximityViolationEvent(
        String userId,
        String otherUserId,
        long distance,
        double threshold,
        String timestamp
) {
}
