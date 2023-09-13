package org.example.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.example.constants.Constants.CLIENT;
import static org.example.constants.Constants.NO_CLARK;
import static org.example.constants.Constants.NO_MESSAGE;
import static org.example.constants.Constants.NO_PUT;
import static org.example.constants.Constants.NO_TRANSACTION;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionControllerTests {
    @Test
    public void testGetAllTransactions() {
        CLIENT.sendAllTransactionsRequest(1L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(1))
                .body("[0].account.id", equalTo(1))
                .body("[0].account.amount", equalTo(20.0F))
                .body("[0].amount", equalTo(20.0F));
        CLIENT.sendAllTransactionsRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(6))
                .body("[0].account.id", equalTo(2))
                .body("[0].amount", equalTo(110.0F))
                .body("[1].amount", equalTo(120.0F))
                .body("[2].amount", equalTo(50.0F))
                .body("[3].amount", equalTo(70.0F))
                .body("[4].amount", equalTo(100.0F));
        CLIENT.sendAllTransactionsRequest(-100L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(7));
        CLIENT.sendAllTransactionsRequest(3L)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo(NO_CLARK));
    }

    @Test
    public void testGetLastTransactions() {
        CLIENT.sendLastTransactionsRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("[0].account.id", equalTo(2))
                .body("[0].amount", equalTo(120.0F))
                .body("[1].amount", equalTo(50.0F))
                .body("[2].amount", equalTo(70.0F))
                .body("[3].amount", equalTo(100.0F))
                .body("[4].amount", equalTo(130.0F));
        CLIENT.sendLastTransactionsRequest(-100L)
                .then()
                .statusCode(NOT_FOUND.value())
                .body("status", equalTo(NOT_FOUND.value()))
                .body("error", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo(NO_MESSAGE));
    }

    @Test
    public void testAddAndDeleteTransaction() {
        CLIENT.sendLastTransactionsRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(5));
        CLIENT.sendAddTransactionsRequest(2L, 100.0)
                .then()
                .statusCode(OK.value())
                .body("amount", equalTo(100.0F))
                .body("transactionType", equalTo("A"))
                .body("account.amount", equalTo(680.0F));
        CLIENT.sendLastTransactionsRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(6));
        CLIENT.sendDeleteTransactionsRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(5));


        CLIENT.sendLastTransactionsRequest(-100L)
                .then()
                .statusCode(NOT_FOUND.value())
                .body("status", equalTo(NOT_FOUND.value()))
                .body("error", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo(NO_MESSAGE));
    }

    @Test
    public void testUpdateTransaction() {
        CLIENT.sendUpdateTransactionRequest(1L, 50.0)
                .then()
                .statusCode(OK.value())
                .body("amount", equalTo(50.0F))
                .body("transactionType", equalTo("U"))
                .body("account.amount", equalTo(50.0F));
        CLIENT.sendUpdateTransactionRequest(1L, 20.0)
                .then()
                .statusCode(OK.value())
                .body("amount", equalTo(20.0F))
                .body("transactionType", equalTo("U"))
                .body("account.amount", equalTo(20.0F));

        CLIENT.sendUpdateTransactionRequest(1L, -999.0)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo(NO_PUT));
        CLIENT.sendUpdateTransactionRequest(3L, 30.0)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo(NO_CLARK));
        CLIENT.sendUpdateTransactionRequest(-100L, -999.0)
                .then()
                .statusCode(NOT_FOUND.value())
                .body("status", equalTo(NOT_FOUND.value()))
                .body("error", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo(NO_MESSAGE));
    }

    @Test
    public void testIncorrectDeleteTransaction() {
        CLIENT.sendDeleteTransactionsRequest(3L)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo(NO_CLARK));
        CLIENT.sendDeleteTransactionsRequest(-100L)
                .then()
                .statusCode(NOT_FOUND.value())
                .body("status", equalTo(NOT_FOUND.value()))
                .body("error", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo(NO_MESSAGE));
    }

    @Test
    public void testTotalDeleteTransaction() {
        CLIENT.sendAllTransactionsRequest(1L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(1));
        CLIENT.sendDeleteTransactionsRequest(1L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(0));
        CLIENT.sendDeleteTransactionsRequest(1L)
                .then()
                .statusCode(INTERNAL_SERVER_ERROR.value())
                .body("status", equalTo(INTERNAL_SERVER_ERROR.value()))
                .body("error", equalTo(INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .body("message", equalTo(NO_TRANSACTION));
        CLIENT.sendAddTransactionsRequest(1L, 20.0)
                .then()
                .statusCode(OK.value())
                .body("amount", equalTo(20.0F))
                .body("transactionType", equalTo("A"))
                .body("account.amount", equalTo(20.0F));
        CLIENT.sendAllTransactionsRequest(1L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(1));
    }
}
