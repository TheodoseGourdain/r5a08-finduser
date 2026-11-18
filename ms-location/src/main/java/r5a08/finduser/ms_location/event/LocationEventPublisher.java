package r5a08.finduser.ms_location.event;

import r5a08.finduser.ms_location.model.Location;

/**
 * Publication des événements de localisation.
 * Le service ne dépend que de cette interface, pas de Kafka (même principe que UserVerifier).
 */
public interface LocationEventPublisher {

    /**
     * Publie un événement user.location.updated pour la localisation donnée.
     * Ne lève pas d'exception : un échec de publication est journalisé.
     */
    void publishLocationUpdated(Location location);
}