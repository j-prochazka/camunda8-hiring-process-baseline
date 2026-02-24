package cz.kyndryl.camunda.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.client.CamundaClient;
import io.camunda.client.api.search.response.Variable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class UserTaskController {

    private final CamundaClient camundaClient;
    private final ObjectMapper objectMapper;

    /**
     * Retrieve all process variables visible to a given user task.
     * Returns a flat JSON object: { "varName": varValue, ... }
     */
    @GetMapping("/{taskKey}/variables")
    public ResponseEntity<Map<String, Object>> getTaskVariables(@PathVariable long taskKey) {
        log.info("Fetching variables for user task {}", taskKey);

        var response = camundaClient.newUserTaskVariableSearchRequest(taskKey)
                .withFullValues()
                .send()
                .join();

        Map<String, Object> variables = new LinkedHashMap<>();
        for (Variable variable : response.items()) {
            try {
                // Variable value is a JSON string — parse it to its native type
                Object parsed = objectMapper.readValue(variable.getValue(), Object.class);
                variables.put(variable.getName(), parsed);
            } catch (Exception e) {
                // If parsing fails, return the raw string value
                variables.put(variable.getName(), variable.getValue());
            }
        }

        log.info("Found {} variables for task {}", variables.size(), taskKey);
        return ResponseEntity.ok(variables);
    }

    @PostMapping("/{taskKey}/complete")
    public ResponseEntity<Void> completeTask(
            @PathVariable long taskKey,
            @RequestBody(required = false) Map<String, Object> variables) {

        log.info("Completing user task {} with variables: {}", taskKey, variables);

        var command = camundaClient.newCompleteUserTaskCommand(taskKey);
        if (variables != null && !variables.isEmpty()) {
            command.variables(variables);
        }
        command.send().join();

        log.info("User task {} completed successfully", taskKey);
        return ResponseEntity.ok().build();
    }
}
