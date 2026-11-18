package r5a08.finduser.ms_proximity_detection.event;

/**
 * Publication des violations de proximité.
 * Le service de détection ne dépend que de cette interface, pas de Kafka.
 */
public interface ViolationEventPublisher {

    /**
     * Publie un événement proximity.violation.detected.
     * Ne lève pas d'exception : un échec de publication est journalisé.
     */
    void publishViolation(ProximityViolationEvent event);
}
