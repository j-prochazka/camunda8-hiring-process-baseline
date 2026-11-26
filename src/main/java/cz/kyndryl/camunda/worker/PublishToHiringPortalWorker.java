package cz.kyndryl.camunda.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.kyndryl.camunda.service.MessageService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PublishToHiringPortalWorker {

    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    @Value("${app.jms.topic.hiring-portal}")
    private String hiringPortalTopic;

    @JobWorker(type = "jprdemo_com.kyndryl.camunda_publishToHiringPortals:1")
    public void publishToHiringPortal(final ActivatedJob job) {
        final Map<String, Object> variables = job.getVariablesAsMap();
        log.info("Publishing job position to hiring portal for process instance: {}", job.getProcessInstanceKey());

        // Build a compact event payload with position details
        final Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventType", "JOB_POSITION_PUBLISHED");
        event.put("jobPositionId", variables.get("jobPositionId"));
        event.put("title", variables.get("title"));
        event.put("department", variables.get("department"));
        event.put("location", variables.get("location"));
        event.put("applicationDeadline", variables.get("applicationDeadline"));
        event.put("processInstanceKey", job.getProcessInstanceKey());

        try {
            final String json = objectMapper.writeValueAsString(event);
            messageService.publishToTopic(hiringPortalTopic, json);
            log.info("Job position published to hiring portal topic: {}", hiringPortalTopic);
        } catch (Exception e) {
            log.error("Failed to publish to hiring portal", e);
            throw new RuntimeException("Failed to publish to hiring portal", e);
        }
    }
}
