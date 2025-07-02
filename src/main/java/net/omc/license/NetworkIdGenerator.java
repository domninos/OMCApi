package net.omc.license;

import java.security.SecureRandom;
import java.time.Instant;

public class NetworkIdGenerator {
    /*
    REF: https://medium.com/@ganesh.shah/uniqueid-generator-in-distributed-systems-implementation-in-java-thread-safe-dbc7ff1fbd36
     */

    private static final long EPOCH = Instant.parse("2024-01-01T00:00:00Z").toEpochMilli();
    private static final int SEQUENCE_BITS = 12;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private static final char[] BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private final String prefix;

    private final long instanceId;
    private long lastTime = -1L;
    private long sequence = 0L;

    public NetworkIdGenerator(String pluginPrefix) {
        this.prefix = pluginPrefix;
        this.instanceId = Math.abs((System.nanoTime() ^ new SecureRandom().nextLong()) % 1024);
    }

    public synchronized String nextId() {
        long now = System.currentTimeMillis();

        if (now < lastTime) {
            return "NULL";
        }

        if (now == lastTime) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) now = waitNextMillis();
        } else {
            sequence = 0;
        }

        lastTime = now;

        long timestamp = now - EPOCH;
        long rawId = (timestamp << SEQUENCE_BITS) | sequence ^ instanceId;

        String base62 = toBase62(rawId);
        if (base62.length() < 8) {
            base62 = "0".repeat(8 - base62.length()) + base62;
        }

        return prefix + base62.substring(0, 8);
    }

    private long waitNextMillis() {
        long now;
        do {
            now = System.currentTimeMillis();
        } while (now <= lastTime);
        return now;
    }

    private String toBase62(long value) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(BASE62[(int) (value % 62)]);
            value /= 62;
        } while (value > 0);
        return sb.reverse().toString();
    }
}
