package r5a08.finduser.ms_location.user;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;
import r5a08.finduser.grpc.user.UserReply;
import r5a08.finduser.grpc.user.UserRequest;
import r5a08.finduser.grpc.user.UserServiceGrpc;
import r5a08.finduser.ms_location.exception.UserServiceUnavailableException;

import java.util.concurrent.TimeUnit;

/**
 * Vérifie l'existence d'un utilisateur en appelant MS-User en gRPC (UserService.GetUser).
 */
@Component
public class GrpcUserVerifier implements UserVerifier {

    /** Délai maximal d'attente de la réponse de MS-User. */
    private static final long DEADLINE_SECONDS = 2;

    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public GrpcUserVerifier(UserServiceGrpc.UserServiceBlockingStub userServiceStub) {
        this.userServiceStub = userServiceStub;
    }

    @Override
    public boolean userExists(String userId) {
        UserRequest request = UserRequest.newBuilder()
                .setId(userId)
                .build();

        try {
            UserReply reply = this.userServiceStub
                    .withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                    .getUser(request);
            return reply.getExists();

        } catch (StatusRuntimeException exception) {
            // Si MS-User signale l'absence par un statut NOT_FOUND plutôt que par exists = false
            if (exception.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return false;
            }
            // UNAVAILABLE (MS-User arrêté), DEADLINE_EXCEEDED (trop lent), etc.
            throw new UserServiceUnavailableException(exception);
        }
    }
}