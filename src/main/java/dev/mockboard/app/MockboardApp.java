package dev.mockboard.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.mockboard.Constants;
import dev.mockboard.Env;
import dev.mockboard.common.faker.TemplateFakerProcessor;
import dev.mockboard.common.validator.MockRuleValidator;
import dev.mockboard.common.validator.RequestMetadataValidator;
import dev.mockboard.repository.BoardRepository;
import dev.mockboard.service.AppSecurityService;
import dev.mockboard.service.BoardService;
import dev.mockboard.service.MockExecutionService;
import dev.mockboard.service.MockRuleService;
import dev.mockboard.service.TemplateFakerService;
import dev.mockboard.service.WebhookService;
import dev.mockboard.web.ErrorHandlers;
import dev.mockboard.web.Routes;
import dev.mockboard.web.api.BoardRoutes;
import dev.mockboard.web.api.MockExecutionRoutes;
import dev.mockboard.web.api.PreRoutes;
import dev.mockboard.web.sse.SseManager;
import dev.mockboard.web.sse.SseRoutes;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinJackson;
import io.javalin.plugin.bundled.CorsPluginConfig;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public final class MockboardApp {

    private MockboardApp() {
    }

    public static Javalin create() {
        var objectMapper = objectMapper();
        var db = mapDb();
        var scheduler = heartbeatScheduler();
        var webhookExecutor = Executors.newVirtualThreadPerTaskExecutor();

        var boardRepository = new BoardRepository(db);
        var appSecurityService = new AppSecurityService();
        var mockRuleValidator = new MockRuleValidator(objectMapper);
        var requestMetadataValidator = new RequestMetadataValidator(objectMapper);
        var templateFakerProcessor = new TemplateFakerProcessor();
        var templateFakerService = new TemplateFakerService(templateFakerProcessor);
        var boardService = new BoardService(boardRepository);
        var mockRuleService = new MockRuleService(mockRuleValidator, boardRepository);
        var sseManager = new SseManager();
        var mockExecutionService = new MockExecutionService(objectMapper, mockRuleService, templateFakerService);
        var webhookService = new WebhookService(sseManager, webhookExecutor);

        scheduler.scheduleAtFixedRate(
                sseManager::sendHeartbeat,
                Constants.SSE_EMITTER_HEARTBEAT_RATE,
                Constants.SSE_EMITTER_HEARTBEAT_RATE,
                TimeUnit.MILLISECONDS
        );

        var lifecycle = new AppLifecycle(sseManager, scheduler, webhookExecutor, db);
        var appRef = new AtomicReference<Javalin>();

        var boardRoutes = new BoardRoutes(boardService, mockRuleService, webhookService);
        var preRoutes = new PreRoutes(appSecurityService);
        var mockExecutionRoutes = new MockExecutionRoutes(requestMetadataValidator, mockExecutionService, webhookService);
        var sseRoutes = new SseRoutes(appSecurityService, boardService, sseManager);
        var routes = new Routes(appSecurityService, boardRoutes, preRoutes, mockExecutionRoutes, sseRoutes);

        var app = Javalin.create(config -> {
            config.startup.showJavalinBanner = false;

            config.jsonMapper(new JavalinJackson(objectMapper, false));
            config.concurrency.useVirtualThreads = true;
            config.http.defaultContentType = ContentType.JSON;
            config.http.maxRequestSize = Constants.MAX_BODY_LENGTH + 1024L;
            config.bundledPlugins.enableCors(cors -> cors.addRule(CorsPluginConfig.CorsRule::anyHost));
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/static";
                staticFiles.location = Location.CLASSPATH;
            });
            config.spaRoot.addFile("/", "/static/index.html", Location.CLASSPATH);
            config.requestLogger.http((ctx, executionTimeMs) -> log.info(
                    "{} {} -> {} ({}ms)",
                    ctx.method(),
                    ctx.path(),
                    ctx.statusCode(),
                    executionTimeMs
            ));
            config.events.serverStopping(lifecycle::closeResources);
            config.routes.apiBuilder(routes::register);
            ErrorHandlers.register(config.routes);
        });

        appRef.set(app);
        lifecycle.registerShutdownHook(appRef::get);
        return app;
    }

    public static int port() {
        return Env.getInt("PORT", 8000);
    }

    private static ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    private static DB mapDb() {
        var file = new File(Constants.STORE_PATH);
        var parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        return DBMaker
                .fileDB(file)
                .fileMmapEnableIfSupported()
                .transactionEnable()
                .make();
    }

    private static ScheduledExecutorService heartbeatScheduler() {
        return Executors.newSingleThreadScheduledExecutor(task -> {
            var thread = new Thread(task, "mockboard-sse-heartbeat");
            thread.setDaemon(true);
            return thread;
        });
    }
}
