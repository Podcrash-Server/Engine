package com.podcrash.api.callback.sources;

import com.podcrash.api.callback.CallbackAction;

/**
 * This class is used to await a certain amount of time
 */
public class AwaitTime extends CallbackAction<AwaitTime> {
    private final long dueTime;

    public AwaitTime(long elapsedTimeMilles) {
        this.dueTime = System.currentTimeMillis() + elapsedTimeMilles;

        this.changeEvaluation(() -> System.currentTimeMillis() > dueTime);
    }
}