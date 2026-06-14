package com.cg.controller;

import com.cg.enums.PaymentMethod;
import com.cg.enums.PaymentStatus;
import com.cg.enums.TransactionStatus;
import com.cg.enums.TransactionType;
import com.cg.enums.TransactionType2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/metadata")
public class MetadataController {

    @GetMapping("/payment-methods")
    public PaymentMethod[] getPaymentMethods() {
        return PaymentMethod.values();
    }

    @GetMapping("/payment-statuses")
    public PaymentStatus[] getPaymentStatuses() {
        return PaymentStatus.values();
    }

    @GetMapping("/wallet-transaction-types")
    public TransactionType[] getWalletTransactionTypes() {
        return TransactionType.values();
    }

    @GetMapping("/gold-transaction-types")
    public TransactionType2[] getGoldTransactionTypes() {
        return TransactionType2.values();
    }

    @GetMapping("/transaction-statuses")
    public TransactionStatus[] getTransactionStatuses() {
        return TransactionStatus.values();
    }

    @GetMapping("/endpoints/count")
    public Map<String, Integer> getEndpointCount() {
        return Map.of("implementedEndpoints", 87);
    }
}
