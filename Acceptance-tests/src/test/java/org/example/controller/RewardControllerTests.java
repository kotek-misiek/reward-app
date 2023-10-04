package org.example.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.example.constants.Constants.CLIENT;
import static org.example.constants.Constants.NO_CLARK;
import static org.example.constants.Constants.NO_MESSAGE;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RewardControllerTests {
    @Test
    public void testGetReward() {
        CLIENT.sendRewardRequest(2L)
                .then()
                .statusCode(OK.value())
                .body(equalTo("270.0"));
        CLIENT.sendRewardRequest(3L)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo(NO_CLARK));
        CLIENT.sendRewardRequest(5L)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo("User with ID = 5 not found"));
        CLIENT.sendRewardRequest(-100L)
                .then()
                .statusCode(NOT_FOUND.value())
                .body("status", equalTo(NOT_FOUND.value()))
                .body("error", equalTo(NOT_FOUND.getReasonPhrase()))
                .body("message", equalTo(NO_MESSAGE));
        CLIENT.sendRewardRequest(null)
                .then()
                .statusCode(BAD_REQUEST.value())
                .body("status", equalTo(BAD_REQUEST.value()))
                .body("error", equalTo(BAD_REQUEST.getReasonPhrase()))
                .body("message", equalTo("Failed to convert value of type 'java.lang.String' " +
                        "to required type 'java.lang.Long'; nested exception is java.lang.NumberFormatException: " +
                        "For input string: \"null\""));
    }

    @Test
    public void testGetTable() {
        CLIENT.sendTableRequest(2L)
                .then()
                .statusCode(OK.value())
                .body("name", equalTo("Howard Stark"))
                .body("monthRate.size()", equalTo(4))
                .body("monthRate[3].month", equalTo("TOTAL"))
                .body("monthRate[3].points", equalTo(270.0F));
        CLIENT.sendTableRequest(-100L)
                .then()
                .statusCode(OK.value())
                .body("size()", equalTo(2))
                .body("[0].name", equalTo("John Wick"))
                .body("[0].monthRate.size()", equalTo(2))
                .body("[0].monthRate[0].points", equalTo(0))
                .body("[0].monthRate[1].month", equalTo("TOTAL"))
                .body("[0].monthRate[1].points", equalTo(0))
                .body("[1].name", equalTo("Howard Stark"))
                .body("[1].monthRate.size()", equalTo(4))
                .body("[1].monthRate[3].month", equalTo("TOTAL"))
                .body("[1].monthRate[3].points", equalTo(270.0F));
    }
}