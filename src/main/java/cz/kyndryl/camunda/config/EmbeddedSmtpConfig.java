package cz.kyndryl.camunda.config;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for embedded GreenMail SMTP server.
 * Only active when 'embedded' profile is used.
 */
@Slf4j
@Configuration
@Profile("embedded")
public class EmbeddedSmtpConfig {

    private GreenMail greenMail;

    @Bean
    public GreenMail greenMail() {
        log.info("Starting embedded GreenMail SMTP server on port 3025");
        ServerSetup serverSetup = new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP);
        greenMail = new GreenMail(serverSetup);
        greenMail.setUser("test@localhost", "test");
        greenMail.start();
        log.info("GreenMail SMTP server started successfully");
        return greenMail;
    }

    @PreDestroy
    public void cleanup() {
        if (greenMail != null) {
            log.info("Stopping GreenMail SMTP server");
            greenMail.stop();
        }
    }
}
