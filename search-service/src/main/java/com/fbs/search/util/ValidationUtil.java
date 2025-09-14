package com.fbs.search.util;

import com.fbs.search.exception.SearchServiceError;
import com.fbs.search.exception.SearchServiceException;
import org.springframework.util.StringUtils;

/**
 * Utility class for validating search request parameters
 */
public class ValidationUtil {

    private ValidationUtil() {
        // Utility class
    }

    /**
     * Validates search criteria parameter
     * @param criteria The search criteria to validate
     * @throws SearchServiceException if criteria is invalid
     */
    public static String validateSearchCriteria(String criteria) {
        if (!StringUtils.hasText(criteria)) {
            return "CHEAPEST"; //default value
        }

        if (!criteria.equalsIgnoreCase("CHEAPEST") && !criteria.equalsIgnoreCase("FASTEST")) {
            throw new SearchServiceException(SearchServiceError.INVALID_SEARCH_CRITERIA);
        }

        return criteria.toUpperCase();
    }
}
