package ma.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import ma.grpc.stubs.Bank;
import ma.grpc.stubs.BankServiceGrpc;

import java.io.IOException;
import java.util.Timer;

public class BankGrpcClient5 {
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

        // send a stream of items to the server
        // we send an observable response which the server will use to send an item in this case
        // and the method will return an request observer which we will use to send a stream of items to the server
        StreamObserver<Bank.ConvertCurrencyRequest> requestStream = asyncStub.fullCurrencyStream(new StreamObserver<Bank.ConvertCurrencyResponse>() {
            @Override
            public void onNext(Bank.ConvertCurrencyResponse convertCurrencyResponse) {
                System.out.println("-----------------");
                System.out.println("response from server: " + convertCurrencyResponse);
                System.out.println("-----------------");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                // this will be called when the server is done sending data
                System.out.println("Client indicate to the server that it is done sending data");
            }
        });

        // send a stream of items to the server
        Timer timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            int counter = 0;
            @Override
            public void run() {
                Bank.ConvertCurrencyRequest request = Bank.ConvertCurrencyRequest.newBuilder()
                        .setCurrencyFrom("USD")
                        .setCurrencyTo("MAD")
                        .setAmount(100)
                        .build();
                requestStream.onNext(request);
                System.out.println("-----------------");
                System.out.println("counter: " + counter);
                System.out.println("-----------------");
                ++counter;
                if (counter == 10) {
                    requestStream.onCompleted();
                    timer.cancel();
                }
            }
        }, 1000, 1000);


        // wait for the response
        // without this line, the JVM will terminate before the response is received
        System.in.read();
    }
}
