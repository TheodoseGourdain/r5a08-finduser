package r5a08.finduser.ms_location.user;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(GrpcUserVerifier.class);

    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public GrpcUserVerifier(UserServiceGrpc.UserServiceBlockingStub userServiceStub) {
        this.userServiceStub = userServiceStub;
    }

    @Override
    public boolean userExists(Long userId) {
        UserRequest request = UserRequest.newBuilder()
                .setId(String.valueOf(userId))
                .build();

        try {
            UserReply reply = this.userServiceStub
                    .withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                    .getUser(request);
            log.info("gRPC GetUser(id={}) -> exists={}", userId, reply.getExists());
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