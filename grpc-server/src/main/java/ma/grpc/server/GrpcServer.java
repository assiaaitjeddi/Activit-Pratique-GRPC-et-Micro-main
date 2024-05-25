package ma.grpc.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import ma.grpc.service.BankGrpcService;

import java.io.IOException;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8084)
                .addService(new BankGrpcService())
                .build();
        server.start();
        System.out.println("Server started at " + server.getPort());

        // continue to listen until the JVM is terminated
        server.awaitTermination();
    }
}
