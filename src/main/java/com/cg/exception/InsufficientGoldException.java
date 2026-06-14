package com.cg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Thrown when:
// 1. User tries to sell more gold than they hold in virtual_gold_holdings
// 2. User tries to convert more gold to physical than they hold
// 3. Branch does not have enough quantity to fulfil a buy request
// Automatically returns HTTP 400
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientGoldException extends RuntimeException {
    public InsufficientGoldException(String message) {
        super(message);
    }
}
