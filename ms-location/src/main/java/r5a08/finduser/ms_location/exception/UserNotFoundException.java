package r5a08.finduser.ms_location.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException (Long userId) {
		super("L'utilisateur " + userId + " n'existe pas.");
	}
}
