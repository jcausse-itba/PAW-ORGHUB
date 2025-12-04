package ar.edu.itba.paw.model.enums;

public enum Language {
    SPANISH,
    ENGLISH;

    private static final Language DEFAULT = SPANISH;
    public static Language getDefault() {
        return DEFAULT;
    }

    public static Language get(final String value) {
        Language toReturn = getDefault();
        if (value == null) {
            return toReturn;
        }
        try {
            toReturn = valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ignored) {}
        return toReturn;
    }
}
