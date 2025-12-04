package ar.edu.itba.paw.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public enum Role {
    USER(0, "USER"),
    PUBLISHER(1, "PUBLISHER"),
    MANAGER(2,  "MANAGER"),
    INSTITUTION_ADMIN(3,   "INSTITUTION_ADMIN"),
    SYSTEM_ADMIN(4, "SYSTEM_ADMIN");

    @Getter private final long roleID;
    @Getter private final String roleName;

    public static Optional<Role> get(final long roleID){
        return Arrays.stream(Role.values())
                .filter(r -> r.roleID == roleID).findFirst();
    }

    public static Optional<Role> get(final String roleName){
        return Arrays.stream(Role.values())
                .filter(r -> r.roleName.equalsIgnoreCase(roleName)).findFirst();
    }
}
