package net.omc.license;

import java.util.Arrays;

public enum Status {
    ACTIVE, REVOKED, PENDING_FOR_ACTIVATION, NULL, DUPLICATE;

    public static Status get(String name) {
        return Arrays.stream(values()).filter((status -> status.name().equalsIgnoreCase(name))).findFirst().orElse(Status.NULL);
    }
}
