package r5a08.finduser.ms_proximity_detection.detection;

import org.springframework.stereotype.Component;

/**
 * Calcul de distance entre deux positions GPS avec la formule de Haversine.
 * Elle donne la distance « à vol d'oiseau » à la surface d'une sphère (la Terre),
 * précise à quelques mètres près pour les distances qui nous intéressent.
 */
@Component
public class DistanceCalculator {

    /** Rayon moyen de la Terre, en mètres. */
    private static final double EARTH_RADIUS_METERS = 6_371_000;

    /** Distance en mètres entre deux positions. */
    public double distanceInMeters(Position a, Position b) {
        double lat1 = Math.toRadians(a.latitude());
        double lat2 = Math.toRadians(b.latitude());
        double deltaLat = Math.toRadians(b.latitude() - a.latitude());
        double deltaLon = Math.toRadians(b.longitude() - a.longitude());

        double h = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double angle = 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));

        return EARTH_RADIUS_METERS * angle;
    }
}
