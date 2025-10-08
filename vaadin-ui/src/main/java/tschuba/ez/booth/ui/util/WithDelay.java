/**
 * Copyright (c) 2025 Thomas Schulte-Bahrenberg
 * All rights reserved.
 */
package tschuba.ez.booth.ui.util;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class WithDelay {
    private static final Logger LOGGER = LoggerFactory.getLogger(WithDelay.class);
    private static final ScheduledExecutorService DEFAULT_EXECUTOR =
            Executors.newSingleThreadScheduledExecutor();

    public static void execute(Duration delay, Runnable action) {
        execute(delay, DEFAULT_EXECUTOR, action);
    }

    public static void execute(
            Duration delay, ScheduledExecutorService executorService, Runnable action) {
        if (delay.isZero() || delay.isNegative()) {
            LOGGER.debug("Executing action WITHOUT delay");
            action.run();
        } else {
            LOGGER.debug("Scheduled execution of action with a delay of {}", delay);
            executorService.schedule(action, delay.toMillis(), TimeUnit.MILLISECONDS);
        }
    }
}
