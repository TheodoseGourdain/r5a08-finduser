package r5a08.finduser.ms_proximity_detection.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import tools.jackson.databind.json.JsonMapper;

/**
 * Implémentation Kafka de ViolationEventPublisher.
 * Même principe que KafkaLocationEventPublisher dans MS-Location : JSON produit avec Jackson,
 * envoyé sous forme de chaîne, userId comme clé.
 */
@Component
public class KafkaViolationEventPublisher implements ViolationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaViolationEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;
    private final String topic;

    public KafkaViolationEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                        JsonMapper jsonMapper,
                                        @Value("${app.kafka.topics.proximity-violation}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.jsonMapper = jsonMapper;
        this.topic = topic;
    }

    @Override
    public void publishViolation(ProximityViolationEvent event) {
        try {
            String json = jsonMapper.writeValueAsString(event);

            kafkaTemplate.send(topic, event.userId(), json)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Échec de publication de {} sur {}", json, topic, ex);
                        } else {
                            log.info("Violation publiée sur {} (partition {}, offset {}) : {}", topic,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset(), json);
                        }
                    });
        } catch (Exception ex) {
            log.error("Impossible de publier la violation {} / {}", event.userId(), event.otherUserId(), ex);
        }
    }
}
