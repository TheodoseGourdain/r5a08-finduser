package r5a08.finduser.ms_proximity_detection.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import r5a08.finduser.ms_proximity_detection.detection.Position;
import r5a08.finduser.ms_proximity_detection.detection.ProximityDetectionService;
import r5a08.finduser.ms_proximity_detection.event.LocationUpdatedEvent;
import tools.jackson.databind.json.JsonMapper;

/**
 * Point d'entrée du microservice : écoute le topic user.location.updated.
 * Son seul rôle est de transformer le message reçu (texte JSON) en objets Java,
 * puis de passer la main au service de détection.
 */
@Component
public class LocationEventListener {

    private static final Logger log = LoggerFactory.getLogger(LocationEventListener.class);

    private final JsonMapper jsonMapper;
    private final ProximityDetectionService detectionService;

    public LocationEventListener(JsonMapper jsonMapper, ProximityDetectionService detectionService) {
        this.jsonMapper = jsonMapper;
        this.detectionService = detectionService;
    }

    /**
     * Appelée par Spring Kafka pour chaque message du topic.
     * Le groupId est défini dans application.yml (spring.kafka.consumer.group-id).
     */
    @KafkaListener(topics = "${app.kafka.topics.location-updated}")
    public void onLocationUpdated(String message) {
        LocationUpdatedEvent event;
        Position position;
        try {
            event = jsonMapper.readValue(message, LocationUpdatedEvent.class);
            position = event.toPosition();
        } catch (RuntimeException ex) {
            // Message illisible : on l'ignore plutôt que de bloquer le topic (voir explications)
            log.warn("Message ignoré, format invalide : {} ({})", message, ex.getMessage());
            return;
        }

        log.info("Position reçue pour l'utilisateur {} : {}", event.userId(), position);
        detectionService.handleLocationUpdate(event.userId(), position);
    }
}
