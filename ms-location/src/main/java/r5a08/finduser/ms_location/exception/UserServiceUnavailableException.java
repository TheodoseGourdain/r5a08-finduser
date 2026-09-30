package r5a08.finduser.ms_location.exception;

import io.grpc.StatusRuntimeException;

/**
 * MS-User n'a pas pu répondre à l'appel gRPC (arrêté, injoignable ou trop lent).
 */
public class UserServiceUnavailableException extends RuntimeException {

    public UserServiceUnavailableException(StatusRuntimeException cause) {
        super("Le service utilisateurs est indisponible (gRPC : " + cause.getStatus().getCode() + ").", cause);
    }
}