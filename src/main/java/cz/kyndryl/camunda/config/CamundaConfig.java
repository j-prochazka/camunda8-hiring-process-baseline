package cz.kyndryl.camunda.config;

import io.camunda.client.annotation.Deployment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@Deployment(resources = {
        "classpath*:/processes/**/*.bpmn",
        "classpath*:/decisions/**/*.dmn",
        "classpath*:/forms/**/*.form"
})
public class CamundaConfig {
}
