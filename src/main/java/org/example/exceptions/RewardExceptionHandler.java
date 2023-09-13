package org.example.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class RewardExceptionHandler {
    private final static Logger LOG = LoggerFactory.getLogger(RewardExceptionHandler.class);

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchExceptionn(MethodArgumentTypeMismatchException ex,
                                                                             HttpServletRequest req) {
        return createResponse(req, ex, BAD_REQUEST);
    }

    @ExceptionHandler(UnacceptableAmountException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleUnacceptableAmountException(UnacceptableAmountException ex,
                                                                    HttpServletRequest req) {
        return createResponse(req, ex, BAD_REQUEST);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException ex,
                                                                     HttpServletRequest req) {
        return createResponse(req, ex, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest req) {
        return createResponse(req, ex, BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    @ResponseBody
    ResponseEntity<ErrorResponse> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex,
                                                                           HttpServletRequest req) {
        return createResponse(req, ex, INTERNAL_SERVER_ERROR);
    }

    protected ResponseEntity<ErrorResponse> createResponse(HttpServletRequest req, Exception ex, HttpStatus httpStatus) {
        var message = ex.getMessage();
        var path = req.getServletPath();
        LOG.error(message);
        return new ResponseEntity<>(
                new ErrorResponse(httpStatus.value(), httpStatus.getReasonPhrase(), message, path), httpStatus);
    }
}
