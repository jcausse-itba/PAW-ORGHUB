package ar.edu.itba.paw.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Status {
    PENDING(0, "PENDING"),
    ACCEPTED(1, "ACCEPTED"),
    REJECTED(2, "REJECTED");

    @Getter
    private final int statusID;
    @Getter private final String statusName;

    public static Status get(final int statusID) {
        return Arrays.stream(Status.values())
                .filter(s -> s.statusID == statusID)
                .findFirst()
                .orElse(PENDING);
    }

    public static Status get(final String statusName) {
        return Arrays.stream(Status.values())
                .filter(s -> s.statusName.equalsIgnoreCase(statusName))
                .findFirst()
                .orElse(PENDING);
    }
}
