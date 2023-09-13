package org.example.controller;

import org.example.client.Client;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class RewardControllerTests {
    private final Client client = new Client();

    private static final String NO_CLARK = "Account belonging to user Clark Kent not found";
    private static final String NO_MESSAGE = "No message available";
    private static final String NO_TRANSACTION = "No such of transaction";
    private static final String NO_PUT = "Request method 'PUT' not supported";

    @Test
    public void testGetReward() {
        client.sendRewardRequest(2L)
                .then()
                .statusCode(OK.value())
                .body(equalTo("270.0"));
        client.sendRewardRequest(3L)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo(NO_CLARK));
        client.sendRewardRequest(5L)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo("User with ID = 5 not found"));
        client.sendRewardRequest(-100L)
                .then()
                .statusCode(NOT_FOUND.value())
                .body("status", equalTo(NOT_FOUND.value()))
                .body("error", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo(NO_MESSAGE));
        client.sendRewardRequest(null)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo("Failed to convert value of type 'java.lang.String' " +
                        "to required type 'java.lang.Long'; nested exception is java.lang.NumberFormatException: " +
                        "For input string: \"null\""));
    }

    @Test
    public void testGetAllTransactions() {
        client.sendAllTransactionsRequest(1L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(1))
                .body("[0].account.id", equalTo(1))
                .body("[0].account.amount", equalTo(20.0F))
                .body("[0].amount", equalTo(20.0F));
        client.sendAllTransactionsRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(6))
                .body("[0].account.id", equalTo(2))
                .body("[0].amount", equalTo(110.0F))
                .body("[1].amount", equalTo(120.0F))
                .body("[2].amount", equalTo(50.0F))
                .body("[3].amount", equalTo(70.0F))
                .body("[4].amount", equalTo(100.0F));
        client.sendAllTransactionsRequest(-100L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(7));
        client.sendAllTransactionsRequest(3L)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo(NO_CLARK));
    }

    @Test
    public void testGetLastTransactions() {
        client.sendLastTransactionsRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("[0].account.id", equalTo(2))
                .body("[0].amount", equalTo(120.0F))
                .body("[1].amount", equalTo(50.0F))
                .body("[2].amount", equalTo(70.0F))
                .body("[3].amount", equalTo(100.0F))
                .body("[4].amount", equalTo(130.0F));
        client.sendLastTransactionsRequest(-100L)
                .then()
                .statusCode(NOT_FOUND.value())
                .body("status", equalTo(NOT_FOUND.value()))
                .body("error", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo(NO_MESSAGE));
    }

    @Test
    public void testAddAndDeleteTransaction() {
        client.sendLastTransactionsRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(5));
        client.sendAddTransactionsRequest(2L, 100.0)
                .then()
                .statusCode(OK.value())
                .body("amount", equalTo(100.0F))
                .body("transactionType", equalTo("A"))
                .body("account.amount", equalTo(680.0F));
        client.sendLastTransactionsRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(6));
        client.sendDeleteTransactionsRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(5));


        client.sendLastTransactionsRequest(-100L)
                .then()
                .statusCode(NOT_FOUND.value())
                .body("status", equalTo(NOT_FOUND.value()))
                .body("error", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo(NO_MESSAGE));
    }

    @Test
    public void testUpdateTransaction() {
        client.sendUpdateTransactionRequest(1L, 50.0)
                .then()
                .statusCode(OK.value())
                .body("amount", equalTo(50.0F))
                .body("transactionType", equalTo("U"))
                .body("account.amount", equalTo(50.0F));
        client.sendUpdateTransactionRequest(1L, 20.0)
                .then()
                .statusCode(OK.value())
                .body("amount", equalTo(20.0F))
                .body("transactionType", equalTo("U"))
                .body("account.amount", equalTo(20.0F));

        client.sendUpdateTransactionRequest(1L, -999.0)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo(NO_PUT));
        client.sendUpdateTransactionRequest(3L, 30.0)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo(NO_CLARK));
        client.sendUpdateTransactionRequest(-100L, -999.0)
                .then()
                .statusCode(NOT_FOUND.value())
                .body("status", equalTo(NOT_FOUND.value()))
                .body("error", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo(NO_MESSAGE));
    }

    @Test
    public void testIncorrectDeleteTransaction() {
        client.sendDeleteTransactionsRequest(3L)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo(NO_CLARK));
        client.sendDeleteTransactionsRequest(-100L)
                .then()
                .statusCode(NOT_FOUND.value())
                .body("status", equalTo(NOT_FOUND.value()))
                .body("error", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo(NO_MESSAGE));
    }

    @Test
    public void testTotalDeleteTransaction() {
        client.sendAllTransactionsRequest(1L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(1));
        client.sendDeleteTransactionsRequest(1L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(0));
        client.sendDeleteTransactionsRequest(1L)
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .body("status", equalTo(INTERNAL_SERVER_ERROR.value()))
                .body("error", equalTo(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body("message", equalTo(NO_TRANSACTION));
        client.sendAddTransactionsRequest(1L, 20.0)
                .then()
                .statusCode(OK.value())
                .body("amount", equalTo(20.0F))
                .body("transactionType", equalTo("A"))
                .body("account.amount", equalTo(20.0F));
        client.sendAllTransactionsRequest(1L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(1));
    }
}