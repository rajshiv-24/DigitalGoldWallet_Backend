package com.cg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Thrown when user's wallet balance (users.balance) is less than the gold purchase cost.
// e.g. wants to buy 10g at ₹6400/g = ₹64,000 but balance is only ₹5,000
// Automatically returns HTTP 400
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
