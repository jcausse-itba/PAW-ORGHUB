package ar.edu.itba.paw.interfaces.enums;

/**
 * Criteria that defines how to filter content by Tag(s). Possible values are {@code ALL} and {@code ANY}.
 */
public enum TagFilterCriteria {
    /**
     * Get content that is tagged with all Tag(s) in a list of Tag(s)
     */
    ALL,

    /**
     * Get content that is tagged with at least one Tag in a list of Tag(s)
     */
    ANY
}
