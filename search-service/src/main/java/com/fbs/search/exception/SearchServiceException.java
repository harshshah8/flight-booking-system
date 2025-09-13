package com.fbs.search.exception;

import com.fbs.GenericException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

@EqualsAndHashCode(callSuper = true)
@Getter
public class SearchServiceException extends GenericException {
    @Serial
    private static final long serialVersionUID = -7326541050669870414L;
    private final SearchServiceError searchServiceError;

    @Builder(builderMethodName = "exceptionBuilder")
    public SearchServiceException(Integer errorCode, String message, String displayMessage, SearchServiceError searchServiceError) {
        super(searchServiceError.getErrorCode(), searchServiceError.getMessage(), searchServiceError.getMessage());
        this.searchServiceError = searchServiceError;
    }

    public static SearchServiceException createException(SearchServiceError error, String displayMessage) {
        return new SearchServiceException(error, displayMessage);
    }

    public static SearchServiceException createException(SearchServiceError error) {
        return new SearchServiceException(error, error.getMessage());
    }

    public SearchServiceException(SearchServiceError error) {
        super(error.getErrorCode(), error.getMessage(), error.getMessage());
        this.searchServiceError = error;
    }

    public SearchServiceException(SearchServiceError error, String message) {
        super(error.getErrorCode(), error.getMessage(), message);
        this.searchServiceError = error;
    }

}
