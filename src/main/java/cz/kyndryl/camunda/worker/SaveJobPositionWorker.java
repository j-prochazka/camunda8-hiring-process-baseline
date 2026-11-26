package cz.kyndryl.camunda.worker;

import cz.kyndryl.camunda.entity.JobPosition;
import cz.kyndryl.camunda.repository.JobPositionRepository;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveJobPositionWorker {

    private final JobPositionRepository jobPositionRepository;

    @JobWorker(type = "jprdemo_com.kyndryl.camunda:saveJobPosition:1")
    public Map<String, Object> saveJobPosition(final ActivatedJob job) {
        final Map<String, Object> variables = job.getVariablesAsMap();
        log.info("Saving job position for process instance: {}", job.getProcessInstanceKey());

        final JobPosition position = JobPosition.builder()
                .title((String) variables.get("title"))
                .description((String) variables.get("description"))
                .department((String) variables.get("department"))
                .location((String) variables.get("location"))
                .requester((String) variables.get("requester"))
                .requesterEmail((String) variables.get("requesterEmail"))
                .applicationDeadline(variables.get("applicationDeadline") != null
                        ? LocalDate.parse((String) variables.get("applicationDeadline")) : null)
                .expectedSalary(variables.get("expectedSalary") != null
                        ? ((Number) variables.get("expectedSalary")).intValue() : null)
                .managerialPosition(variables.get("managerialPosition") != null
                        ? (Boolean) variables.get("managerialPosition") : false)
                .processInstanceKey(String.valueOf(job.getProcessInstanceKey()))
                .build();

        final JobPosition saved = jobPositionRepository.save(position);
        log.info("Job position saved with id: {}", saved.getId());

        // Return the DB id so downstream tasks can use it to query applications
        return Map.of("jobPositionId", saved.getId());
    }
}
