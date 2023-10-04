package org.example.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class Client {
    protected static final String PROPERTY_BASE_URL = System.getProperty("serviceUrl");
    private static final String REWARD = "/reward";
    private static final String ALL_TRANSACTIONS = "/transactions/all";
    private static final String TRANSACTIONS = "/transactions";

    private RequestSpecification createRequest() {
        RestAssured.baseURI = PROPERTY_BASE_URL;
        return RestAssured.given();
    }

    public Response sendRewardRequest(Long userId) {
        final var request = createRequest();
        return Long.valueOf(-100L).equals(userId)
                ? request.get(REWARD)
                : request.get(REWARD + "/" + userId);
    }

    public Response sendTableRequest(Long userId) {
        final var request = createRequest();
        return Long.valueOf(-100L).equals(userId)
                ? request.get(REWARD + "/table")
                : request.get(REWARD + "/table/" + userId);
    }

    public Response sendAllTransactionsRequest(Long userId) {
        final var request = createRequest();
        return Long.valueOf(-100L).equals(userId)
                ? request.get(ALL_TRANSACTIONS)
                : request.get(ALL_TRANSACTIONS + "/" + userId);
    }

    public Response sendLastTransactionsRequest(Long userId) {
        final var request = createRequest();
        return Long.valueOf(-100L).equals(userId)
                ? request.get(TRANSACTIONS)
                : request.get(TRANSACTIONS + "/" + userId);
    }

    public Response sendAddTransactionsRequest(Long userId, Double amount) {
        final var request = createRequest();
        return Long.valueOf(-100L).equals(userId)
                ? request.post(TRANSACTIONS)
                : request.post(TRANSACTIONS + "/" + userId + "/" + amount);
    }

    public Response sendUpdateTransactionRequest(Long userId, Double amount) {
        final var request = createRequest();
        return Long.valueOf(-100L).equals(userId)
                ? request.put(TRANSACTIONS)
                : request.put(TRANSACTIONS + "/" + userId
                + (Double.valueOf(amount).equals(-999.0) ? "" : "/" + amount));
    }

    public Response sendDeleteTransactionsRequest(Long userId) {
        final var request = createRequest();
        return Long.valueOf(-100L).equals(userId)
                ? request.delete(TRANSACTIONS)
                : request.delete(TRANSACTIONS + "/" + userId);
    }
}
