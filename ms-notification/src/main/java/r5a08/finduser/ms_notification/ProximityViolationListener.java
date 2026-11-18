package r5a08.finduser.ms_notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProximityViolationListener {

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
				Distance mesurée : %s m
				Seuil autorisé : %s m
				Heure : %s
				""".formatted(
				event.userId(),
				event.otherUserId(),
				event.distance(),
				event.threshold(),
				event.timestamp()));

		mailSender.send(message);
	}
}
