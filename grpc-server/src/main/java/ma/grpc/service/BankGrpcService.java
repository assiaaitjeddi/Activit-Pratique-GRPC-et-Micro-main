package ma.grpc.service;

import io.grpc.stub.StreamObserver;
import ma.grpc.stubs.Bank;
import ma.grpc.stubs.BankServiceGrpc;

import java.util.Timer;

public class BankGrpcService extends BankServiceGrpc.BankServiceImplBase {
    // Implement the service methods here
    @Override
    public void convert(Bank.ConvertCurrencyRequest request, StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        String currencyFrom = request.getCurrencyFrom();
        String currencyTo = request.getCurrencyTo();
        double amount = request.getAmount();

        Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                .setCurrencyFrom(currencyFrom)
                .setCurrencyTo(currencyTo)
                .setAmount(amount)
                .setResult(amount * 11.4)
                .build();
        // send item to client
        responseObserver.onNext(response);
        // on completed means that the server has finished sending data
        responseObserver.onCompleted();
    }

    @Override
    public void getCurrentCurrencyStream(Bank.ConvertCurrencyRequest request, StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        String currencyFrom = request.getCurrencyFrom();
        String currencyTo = request.getCurrencyTo();
        double amount = request.getAmount();

        // send a stream of items to the client
        Timer timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            int counter = 0;
            @Override
            public void run() {
                Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                        .setCurrencyFrom(currencyFrom)
                        .setCurrencyTo(currencyTo)
                        .setAmount(amount)
                        .setResult(amount * Math.random()*10)
                        .build();
                responseObserver.onNext(response);
                ++counter;
                if (counter == 10) {
                    responseObserver.onCompleted();
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }

    @Override
    public StreamObserver<Bank.ConvertCurrencyRequest> performStream(StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        return new StreamObserver<Bank.ConvertCurrencyRequest>() {
            double sum = 0;
            @Override
            public void onNext(Bank.ConvertCurrencyRequest convertCurrencyRequest) {
                // each time the client sends an item, calculate the sum
                sum += convertCurrencyRequest.getAmount();
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                // send the sum to the client
                Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                        .setResult(sum)
                        .build();

                responseObserver.onNext(response);

                // on completed means that the server has finished sending data even the server is sending just one item
                responseObserver.onCompleted();

            }
        };

    }

    @Override
    public StreamObserver<Bank.ConvertCurrencyRequest> fullCurrencyStream(StreamObserver<Bank.ConvertCurrencyResponse> responseObserver) {
        return new StreamObserver<Bank.ConvertCurrencyRequest>() {
            @Override
            public void onNext(Bank.ConvertCurrencyRequest convertCurrencyRequest) {
                // send the item back to the client
                Bank.ConvertCurrencyResponse response = Bank.ConvertCurrencyResponse.newBuilder()
                        .setCurrencyFrom(convertCurrencyRequest.getCurrencyFrom())
                        .setCurrencyTo(convertCurrencyRequest.getCurrencyTo())
                        .setAmount(convertCurrencyRequest.getAmount())
                        .setResult(convertCurrencyRequest.getAmount() * 11.4)
                        .build();
                responseObserver.onNext(response);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {
                // when the client decides to stop sending data
                // the server completes the response
                responseObserver.onCompleted();
            }
        };
    }
}
