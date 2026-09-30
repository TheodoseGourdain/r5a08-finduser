package r5a08.finduser.ms_location.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;
import r5a08.finduser.grpc.user.UserServiceGrpc;

/**
 * Crée le stub client gRPC vers MS-User.
 * L'adresse du serveur est lue dans spring.grpc.client.channel.user-service.target.
 */
@Configuration
public class GrpcClientConfig {

    @Bean
    public UserServiceGrpc.UserServiceBlockingStub userServiceStub(GrpcChannelFactory channels) {
        return UserServiceGrpc.newBlockingStub(channels.createChannel("user-service"));
    }
}