package r5a08.finduser.ms_notification;

import java.time.Instant;

public record ProximityViolationEvent(
		String userId,
		String otherUserId,
		double distance,
		double threshold,
		Instant timestamp) {
}
