package r5a08.finduser.ms_location.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import r5a08.finduser.ms_location.model.Location;
import tools.jackson.databind.json.JsonMapper;

/**
 * Implémentation Kafka de LocationEventPublisher.
 * Le JSON est produit ici avec Jackson et envoyé sous forme de chaîne (StringSerializer par défaut),
 * pour éviter les en-têtes de type Java ajoutés par le JsonSerializer de Spring Kafka.
 */
@Component
public class KafkaLocationEventPublisher implements LocationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaLocationEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;
    private final String topic;

    public KafkaLocationEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                       JsonMapper jsonMapper,
                                       @Value("${app.kafka.topics.location-updated}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
        this.topic = topic;
    }

    @Override
    public void publishLocationUpdated(Location location) {
        LocationUpdatedEvent event = LocationUpdatedEvent.fromEntity(location);
        try {
            String json = jsonMapper.writeValueAsString(event);

            // userId comme clé : tous les événements d'un utilisateur vont dans la même partition (ordre garanti)
            kafkaTemplate.send(topic, event.userId(), json)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            // Échec asynchrone (broker injoignable après l'envoi, timeout...)
                            log.error("Échec de publication de {} sur {}", json, topic, ex);
                        } else {
                            log.info("Publié sur {} (partition {}, offset {}) : {}", topic,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset(), json);
                        }
                    });
        } catch (Exception ex) {
            // Échec synchrone : sérialisation, ou métadonnées du topic indisponibles après max.block.ms (5 s)
            log.error("Impossible de publier l'événement pour l'utilisateur {}", event.userId(), ex);
        }
    }
}