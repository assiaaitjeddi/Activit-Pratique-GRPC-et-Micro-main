package ma.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import ma.grpc.stubs.Bank;
import ma.grpc.stubs.BankServiceGrpc;

import java.io.IOException;

public class BankGrpcClient2 {
    public static void main(String[] args) throws IOException {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8084)
                .usePlaintext()
                .build();

        // async stub
        BankServiceGrpc.BankServiceStub asyncStub = BankServiceGrpc.newStub(managedChannel);
        Bank.ConvertCurrencyRequest request = Bank.ConvertCurrencyRequest.newBuilder()
                .setCurrencyFrom("USD")
                .setCurrencyTo("MAD")
                .setAmount(100)
                .build();

        asyncStub.convert(request, new StreamObserver<Bank.ConvertCurrencyResponse>() {
            @Override
            public void onNext(Bank.ConvertCurrencyResponse convertCurrencyResponse) {
                System.out.println(convertCurrencyResponse);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                // this will be called when the server is done sending data
                System.out.println("Server is done sending data");
            }
        }
        );

        System.out.println("We will see this before the response, because the response is async");

        // wait for the response
        // without this line, the JVM will terminate before the response is received
        System.in.read();
    }
}
