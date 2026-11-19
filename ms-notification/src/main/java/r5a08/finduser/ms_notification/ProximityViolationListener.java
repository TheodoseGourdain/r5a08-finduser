package r5a08.finduser.ms_notification;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProximityViolationListener {

    /** Fuseau d'affichage des dates dans les emails (les événements sont en UTC). */
    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");

    /** Format lisible, par exemple « 19/11/2026 à 10:22:37 ». */
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm:ss");

    private final ObjectMapper objectMapper;
    private final JavaMailSender mailSender;
    private final String adminEmail;

    public ProximityViolationListener(
            ObjectMapper objectMapper,
            JavaMailSender mailSender,
            @Value("${notification.admin-email}") String adminEmail) {
        this.objectMapper = objectMapper;
        this.mailSender = mailSender;
        this.adminEmail = adminEmail;
    }

    @KafkaListener(topics = "proximity.violation.detected", groupId = "ms-notification")
    public void onViolation(String payload) {
        ProximityViolationEvent event = objectMapper.readValue(payload, ProximityViolationEvent.class);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("notification@finduser.local");
        message.setTo(adminEmail);
        message.setSubject("Violation de proximité détectée");
        message.setText("""
                Une violation de proximité a été détectée.

                Utilisateur : %s
                Autre utilisateur : %s
                Distance mesurée : %.0f m
                Seuil autorisé : %.0f m
                Date : %s
                """.formatted(
                event.userId(),
                event.otherUserId(),
                event.distance(),
                event.threshold(),
                formatDate(event.timestamp())));

        mailSender.send(message);
    }

    /**
     * Convertit l'instant de l'événement (UTC) en date lisible à l'heure de Paris.
     */
    private String formatDate(Instant timestamp) {
        if (timestamp == null) {
            return "inconnue";
        }
        return timestamp.atZone(ZONE_PARIS).format(DATE_FORMAT) + " (heure de Paris)";
    }
}