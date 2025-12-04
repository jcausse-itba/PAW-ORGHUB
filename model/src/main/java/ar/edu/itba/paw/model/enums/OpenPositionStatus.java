package ar.edu.itba.paw.model.enums;

public enum OpenPositionStatus {
    ACTIVE,
    CLOSED,
    PAUSED;

    private static final OpenPositionStatus DEFAULT = ACTIVE;
    
    public static OpenPositionStatus getDefault() {
        return DEFAULT;
    }

    public static OpenPositionStatus get(final String value) {
        OpenPositionStatus toReturn = getDefault();
        if (value == null) {
            return toReturn;
        }
        try {
            toReturn = valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ignored) {}
        return toReturn;
    }
}
