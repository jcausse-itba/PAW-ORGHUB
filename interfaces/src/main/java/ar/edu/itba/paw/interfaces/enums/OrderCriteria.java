package ar.edu.itba.paw.interfaces.enums;

import lombok.RequiredArgsConstructor;

/**
 * Criteria that defines how to order content. Possible values are {@code NEWEST_FIRST} and {@code OLDEST_FIRST}.
 * {@code OrderCriteria.getDefault()} can be used to obtain a default value, which is {@code NEWEST_FIRST}.
 */
@RequiredArgsConstructor
public enum OrderCriteria {
    /**
     * Order by date, descending
     */
    NEWEST_FIRST("DESC"),

    /**
     * Order by date, ascending
     */
    OLDEST_FIRST("ASC");

    private final String sql;

    public String toSQL() {
        return sql;
    }

    public static OrderCriteria getDefault() {
        return NEWEST_FIRST;
    }
}
