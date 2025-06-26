package net.omc.util;

public class TokenProvider<T extends String> {
    private LicenseConfig config;

    public TokenProvider<T> setConfig(LicenseConfig config) {
        this.config = config;
        return this;
    }

    public String get(String key) {
        return this.config.get(key);
    }
}
