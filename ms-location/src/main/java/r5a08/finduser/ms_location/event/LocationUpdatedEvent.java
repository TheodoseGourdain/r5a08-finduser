package r5a08.finduser.ms_location.event;

import java.time.ZoneId;

import r5a08.finduser.ms_location.model.Location;

/**
 * Événement publié dans le topic user.location.updated.
 * Contrat commun avec MS-Proximity-Detection : ne pas modifier sans se concerter.
 *
 * Exemple :
 * {"userId": "123", "latitude": 47.2184, "longitude": -1.5536, "timestamp": "2026-01-10T10:15:30Z"}
 */
public record LocationUpdatedEvent(
        String userId,
        double latitude,
        double longitude,
        String timestamp
) {

    private static final ZoneId ZONE_PARIS = ZoneId.of("Europe/Paris");

    /**
     * Construit l'événement à partir d'une localisation enregistrée.
     * dateHeure est un LocalDateTime à l'heure de Paris : on le convertit en instant UTC (format ISO, suffixe Z).
     */
    public static LocationUpdatedEvent fromEntity(Location location) {
        return new LocationUpdatedEvent(
                String.valueOf(location.getUserId()),
                location.getLatitude(),
                location.getLongitude(),
                location.getDateHeure().atZone(ZONE_PARIS).toInstant().toString()
        );
    }
}