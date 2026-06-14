package com.cg.service;

import com.cg.dto.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {

    List<PaymentResponseDTO> getPaymentsByUser(Integer userId);

    
    List<PaymentResponseDTO> getAllPayments();

   
    PaymentResponseDTO getPaymentById(Integer paymentId);
}
