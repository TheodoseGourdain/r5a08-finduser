package r5a08.finduser.ms_notification;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ProximityViolationListenerTests {

	@Test
	void sendsAnEmailContainingTheViolationDetails() {
		JavaMailSender mailSender = mock(JavaMailSender.class);
		ProximityViolationListener listener = new ProximityViolationListener(
				JsonMapper.builder().findAndAddModules().build(), mailSender, "admin@example.com");

		listener.onViolation("""
				{"userId":"1","otherUserId":"2","distance":85,"threshold":100,"timestamp":"2026-10-15T10:00:00Z"}
				""");

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(captor.capture());
		SimpleMailMessage message = captor.getValue();
		assertThat(message.getTo()).containsExactly("admin@example.com");
		assertThat(message.getSubject()).isEqualTo("Violation de proximité détectée");
		assertThat(message.getText())
				.contains("Utilisateur : 1")
				.contains("Autre utilisateur : 2")
				.contains("Distance mesurée : 85.0 m")
				.contains("Seuil autorisé : 100.0 m")
				.contains("Heure : 2026-10-15T10:00:00Z");
	}
}
