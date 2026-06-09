package dev.mockboard.app;

import dev.mockboard.web.sse.SseManager;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Slf4j
public class AppLifecycle {

    private final SseManager sseManager;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService webhookExecutor;
    private final DB db;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public AppLifecycle(SseManager sseManager,
                        ScheduledExecutorService scheduler,
                        ExecutorService webhookExecutor,
                        DB db) {
        this.sseManager = sseManager;
        this.scheduler = scheduler;
        this.webhookExecutor = webhookExecutor;
        this.db = db;
    }

    public void registerShutdownHook(Supplier<Javalin> appSupplier) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("JVM shutdown requested");
            stopApp(appSupplier);
            closeResources();
        }, "mockboard-shutdown"));
    }

    public void closeResources() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }

        closeSse();
        shutdownScheduler();
        shutdownExecutor();
        closeDb();
    }

    private void stopApp(Supplier<Javalin> appSupplier) {
        try {
            var app = appSupplier.get();
            if (app != null) {
                log.info("Stopping Javalin");
                app.stop();
            }
        } catch (Exception e) {
            log.warn("Unable to stop Javalin cleanly: {}", e.getMessage(), e);
        }
    }

    private void closeSse() {
        try {
            log.info("Closing SSE clients");
            sseManager.onShutdown();
        } catch (Exception e) {
            log.warn("Unable to close SSE clients cleanly: {}", e.getMessage(), e);
        }
    }

    private void shutdownScheduler() {
        try {
            log.info("Stopping heartbeat scheduler");
            scheduler.shutdownNow();
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Unable to stop scheduler cleanly: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    private void shutdownExecutor() {
        try {
            log.info("Stopping webhook executor");
            webhookExecutor.shutdown();
            if (!webhookExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                webhookExecutor.shutdownNow();
            }
        } catch (Exception e) {
            log.warn("Unable to stop webhook executor cleanly: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    private void closeDb() {
        try {
            log.info("Committing and closing MapDB");
            db.commit();
            db.close();
        } catch (Exception e) {
            log.warn("Unable to close MapDB cleanly: {}", e.getMessage(), e);
        }
    }
}
