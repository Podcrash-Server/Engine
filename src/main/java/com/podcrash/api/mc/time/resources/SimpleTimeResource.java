package com.podcrash.api.mc.time.resources;

public interface SimpleTimeResource extends TimeResource {
    @Override
    void task();

    @Override
    default boolean cancel() {
        return false;
    }

    @Override
    default void cleanup() {

    }
}
