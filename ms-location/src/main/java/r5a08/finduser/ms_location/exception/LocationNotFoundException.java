package r5a08.finduser.ms_location.exception;

public class LocationNotFoundException extends RuntimeException{

    public LocationNotFoundException (Long locationId) {
		super("La localisation " + locationId + " n'existe pas.");
	}
}
