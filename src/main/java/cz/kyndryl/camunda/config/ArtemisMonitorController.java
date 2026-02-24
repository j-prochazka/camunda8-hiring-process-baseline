package cz.kyndryl.camunda.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.api.core.management.QueueControl;
import org.apache.activemq.artemis.api.core.management.ResourceNames;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/artemis")
@Profile("embedded")
@RequiredArgsConstructor
public class ArtemisMonitorController {

    private final EmbeddedActiveMQ embeddedActiveMQ;

    @GetMapping("/queues")
    public ResponseEntity<List<Map<String, Object>>> listQueues() {
        List<Map<String, Object>> queues = new ArrayList<>();

        for (Object resource : embeddedActiveMQ.getActiveMQServer().getManagementService().getResources(QueueControl.class)) {
            QueueControl queueControl = (QueueControl) resource;
            Map<String, Object> info = new LinkedHashMap<>();
            info.put("name", queueControl.getName());
            info.put("address", queueControl.getAddress());
            info.put("routingType", queueControl.getRoutingType());
            info.put("messageCount", queueControl.getMessageCount());
            info.put("consumerCount", queueControl.getConsumerCount());
            info.put("messagesAdded", queueControl.getMessagesAdded());
            info.put("deliveringCount", queueControl.getDeliveringCount());
            info.put("messagesAcknowledged", queueControl.getMessagesAcknowledged());
            queues.add(info);
        }

        return ResponseEntity.ok(queues);
    }

    @GetMapping("/queues/{queueName}/messages")
    public ResponseEntity<List<Map<String, Object>>> browseMessages(
            @PathVariable String queueName,
            @RequestParam(defaultValue = "100") int limit) throws Exception {

        QueueControl queueControl = (QueueControl) embeddedActiveMQ.getActiveMQServer()
                .getManagementService().getResource(ResourceNames.QUEUE + queueName);

        if (queueControl == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object>[] messages = queueControl.listMessages("");
        int count = Math.min(messages.length, limit);

        List<Map<String, Object>> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(messages[i]);
        }

        return ResponseEntity.ok(result);
    }
}
