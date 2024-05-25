package ma.grpc.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ma.grpc.stubs.Bank;
import ma.grpc.stubs.BankServiceGrpc;

public class BankGrpcClient1 {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8084)
                .usePlaintext()
                .build();

        // blocking stub means that the client will wait for the server to respond
        BankServiceGrpc.BankServiceBlockingStub bankServiceBlockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        Bank.ConvertCurrencyRequest convertCurrencyRequest = Bank.ConvertCurrencyRequest.newBuilder()
                .setCurrencyFrom("USD")
                .setCurrencyTo("MAD")
                .setAmount(100)
                .build();

        Bank.ConvertCurrencyResponse currencyResponse = bankServiceBlockingStub.convert(convertCurrencyRequest);

        System.out.println("Currency response: \n" + currencyResponse);
    }
}
