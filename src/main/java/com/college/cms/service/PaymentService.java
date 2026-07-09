package com.college.cms.service;

import com.college.cms.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Payment savePayment(Payment payment);

    List<Payment> getAllPayments();

    Optional<Payment> getPaymentById(Integer paymentId);

    Payment updatePayment(Integer paymentId, Payment payment);

    void deletePayment(Integer paymentId);

}