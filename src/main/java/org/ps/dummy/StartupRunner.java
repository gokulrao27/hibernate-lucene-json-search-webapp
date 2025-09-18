package org.ps.dummy;

import org.ps.dummy.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@ConditionalOnProperty(prefix = "app.external", name = "fetch-on-startup", havingValue = "true", matchIfMissing = true)
@Component
@EnableAsync
public class StartupRunner implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger log = LoggerFactory.getLogger(StartupRunner.class);

    private final UserService userService;

    @Value("${app.external.fetch-on-startup:true}")
    private boolean fetchOnStartup;

    @Autowired
    public StartupRunner(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (!fetchOnStartup) {
            log.info("Startup fetch-on-startup disabled, skipping external fetch");
            return;
        }
        log.info("Application ready event received - scheduling external fetch");
        doFetchAsync();
    }

    @Async
    public void doFetchAsync() {
        try {
            userService.fetchExternalUsers();
            log.info("External users fetched and saved");
        } catch (Exception e) {
            log.error("Failed to fetch external users on startup: {}", e.toString());
        }
    }
}
