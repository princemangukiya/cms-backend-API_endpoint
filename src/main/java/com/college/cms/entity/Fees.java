package com.college.cms.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "fees_detail")
public class Fees {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private Long feeId;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "scholarship")
    private Double scholarship;

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "total_fees")
    private Double totalFees;

    public Fees() {
    }

    public Long getFeeId() {
        return feeId;
    }

    public void setFeeId(Long feeId) {
        this.feeId = feeId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Double getScholarship() {
        return scholarship;
    }

    public void setScholarship(Double scholarship) {
        this.scholarship = scholarship;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Double getTotalFees() {
        return totalFees;
    }

    public void setTotalFees(Double totalFees) {
        this.totalFees = totalFees;
    }

    @Transient
    @JsonProperty("paid_amount")
    @JsonAlias({"paidAmount", "paid_amount"})
    private Double paidAmount = 0.0;

    @Transient
    @JsonProperty("pending_due")
    @JsonAlias({"pendingDue", "pending_due"})
    private Double pendingDue = 0.0;

    @Transient
    @JsonProperty("payment_status")
    @JsonAlias({"paymentStatus", "payment_status", "status"})
    private String paymentStatus = "Pending";

    public Double getPaidAmount() {
        return paidAmount != null ? paidAmount : 0.0;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Double getPendingDue() {
        return pendingDue != null ? pendingDue : 0.0;
    }

    public void setPendingDue(Double pendingDue) {
        this.pendingDue = pendingDue;
    }

    public String getPaymentStatus() {
        return paymentStatus != null ? paymentStatus : "Pending";
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}