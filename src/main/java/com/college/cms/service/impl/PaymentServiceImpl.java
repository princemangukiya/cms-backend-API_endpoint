package com.college.cms.service.impl;

import com.college.cms.entity.Payment;
import com.college.cms.repository.PaymentRepository;
import com.college.cms.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> getPaymentById(Integer paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public Payment updatePayment(Integer paymentId, Payment payment) {

        Payment existing = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment Not Found"));

        existing.setFeeId(payment.getFeeId());
        existing.setStudentId(payment.getStudentId());
        existing.setPaidAmount(payment.getPaidAmount());
        existing.setDate(payment.getDate());
        existing.setPaymentMode(payment.getPaymentMode());
        existing.setTransactionId(payment.getTransactionId());
        existing.setStatus(payment.getStatus());

        return paymentRepository.save(existing);
    }

    @Override
    public void deletePayment(Integer paymentId) {

        Payment existing = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment Not Found"));

        paymentRepository.delete(existing);
    }
}