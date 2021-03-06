package net.minestom.server.utils.time;

import net.minestom.server.MinecraftServer;

public enum TimeUnit {

    TICK, HOUR, MINUTE, SECOND, MILLISECOND;

    public long toMilliseconds(long value) {
        switch (this) {
            case TICK:
                return MinecraftServer.TICK_MS * value;
            case HOUR:
                return value * 3_600_000;
            case MINUTE:
                return value * 60_000;
            case SECOND:
                return value * 1000;
            case MILLISECOND:
                return value;
            default:
                return -1; // Unexpected
        }
    }

}
