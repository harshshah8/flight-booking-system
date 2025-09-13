package com.fbs.payment.exception;

import com.fbs.GenericException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Getter
public class PaymentServiceException extends GenericException {

    @Serial
    private static final long serialVersionUID = -4177774907906489211L;
    private final PaymentServiceError paymentServiceError;

    public PaymentServiceException(PaymentServiceError error) {
        super(error.getErrorCode(), error.getMessage(), error.getMessage());
        this.paymentServiceError = error;
    }

    public PaymentServiceException(PaymentServiceError error, String message) {
        super(error.getErrorCode(), error.getMessage(), message);
        this.paymentServiceError = error;
    }

    public PaymentServiceError getPaymentServiceError() {
        return paymentServiceError;
    }
}