package cz.kyndryl.camunda.controller;

import cz.kyndryl.camunda.entity.JobApplication;
import cz.kyndryl.camunda.repository.JobApplicationRepository;
import io.camunda.client.CamundaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationRepository jobApplicationRepository;
    private final CamundaClient camundaClient;

    @GetMapping
    public List<JobApplication> listAll(@RequestParam(required = false) Long positionId) {
        if (positionId != null) {
            return jobApplicationRepository.findByJobPositionId(positionId);
        }
        return jobApplicationRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getById(@PathVariable Long id) {
        return jobApplicationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Submit a job application. This correlates a Camunda message to the waiting
     * Application Processing process instance, passing the candidate data as variables.
     * The BPMN service task "Save Application to Database" will then persist it.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitApplication(@RequestBody Map<String, Object> application) {
        final String candidateName = (String) application.get("candidateName");
        final String candidateEmail = (String) application.get("candidateEmail");
        final Object jobPositionId = application.get("jobPositionId");

        log.info("Application received from {} ({}) for position {}", candidateName, candidateEmail, jobPositionId);

        // Build the variables to pass with the message
        final Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("candidateName", candidateName);
        variables.put("candidateEmail", candidateEmail);
        variables.put("resumeUrl", application.get("resumeUrl"));
        variables.put("coverLetter", application.get("coverLetter"));
        variables.put("jobPositionId", jobPositionId);

        //TODO implement message send

        log.info("Message correlated for position {}", jobPositionId);

        return ResponseEntity.ok(Map.of(
                "status", "submitted",
                "candidateName", candidateName,
                "jobPositionId", jobPositionId
        ));
    }
}
