package tschuba.ez.booth.ui.util;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class WithDelay {
    private final static Logger LOGGER = LoggerFactory.getLogger(WithDelay.class);
    private final static ScheduledExecutorService DEFAULT_EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    public static void execute(Duration delay, Runnable action) {
        execute(delay, DEFAULT_EXECUTOR, action);
    }

    public static void execute(Duration delay, ScheduledExecutorService executorService, Runnable action) {
        if (delay.isZero() || delay.isNegative()) {
            LOGGER.debug("Executing action WITHOUT delay");
            action.run();
        } else {
            LOGGER.debug("Scheduled execution of action with a delay of {}", delay);
            executorService.schedule(action, delay.toMillis(), TimeUnit.MILLISECONDS);
        }
    }
}