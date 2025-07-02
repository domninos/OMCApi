package net.omc.license;

public class License {
    private String key;
    private final Status status;

    public License(Status status) {
        this.status = status;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public boolean isValid() {
        return status == Status.ACTIVE && key != null;
    }
}
