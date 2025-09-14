package com.fbs.booking.exception;

import com.fbs.GenericException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Getter
public class BookingServiceException extends GenericException {

    @Serial
    private static final long serialVersionUID = -4177774907906489210L;
    private final BookingServiceError bookingServiceError;

    public BookingServiceException(BookingServiceError error) {
        super(error.getErrorCode(), error.getMessage(), error.getMessage());
        this.bookingServiceError = error;
    }

    public BookingServiceException(BookingServiceError error, String message) {
        super(error.getErrorCode(), error.getMessage(), message);
        this.bookingServiceError = error;
    }

    public BookingServiceError getBookingServiceError() {
        return bookingServiceError;
    }
}