package r5a08.finduser.ms_user.grpc;

import org.springframework.grpc.server.service.GrpcService;

import io.grpc.stub.StreamObserver;

import r5a08.finduser.ms_user.entity.User;
import r5a08.finduser.ms_user.service.UserService;


@GrpcService
public class UserGrpcService extends UserServiceGrpc.UserServiceImplBase {

    private final UserService userService;

    public UserGrpcService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void getUser(
            UserRequest request,
            StreamObserver<UserReply> responseObserver) {

        try {
            Long id = Long.parseLong(request.getId());

            User user = userService.getUser(id);

            UserReply reply;

            if (user == null) {
                reply = UserReply.newBuilder()
                        .setId(request.getId())
                        .setExists(false)
                        .build();
            } else {
                reply = UserReply.newBuilder()
                        .setId(user.getId().toString())
                        .setPrenom(user.getPrenom())
                        .setNom(user.getNom())
                        .setExists(true)
                        .build();
            }

            responseObserver.onNext(reply);
            responseObserver.onCompleted();

        } catch (NumberFormatException e) {
            responseObserver.onError(
                    io.grpc.Status.INVALID_ARGUMENT
                            .withDescription("L'id doit être un nombre")
                            .asRuntimeException());
        }
    }
}