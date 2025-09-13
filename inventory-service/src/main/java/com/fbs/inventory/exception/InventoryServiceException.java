package com.fbs.inventory.exception;

import com.fbs.GenericException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Getter
public class InventoryServiceException extends GenericException {

    @Serial
    private static final long serialVersionUID = -4177774907906489209L;
    private final InventoryServiceError inventoryServiceError;

    public InventoryServiceException(InventoryServiceError error) {
        super(error.getErrorCode(), error.getMessage(), error.getMessage());
        this.inventoryServiceError = error;
    }

    public InventoryServiceException(InventoryServiceError error, String message) {
        super(error.getErrorCode(), error.getMessage(), message);
        this.inventoryServiceError = error;
    }

    public InventoryServiceError getInventoryServiceError() {
        return inventoryServiceError;
    }
}