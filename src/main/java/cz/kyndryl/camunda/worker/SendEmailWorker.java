package cz.kyndryl.camunda.worker;

import cz.kyndryl.camunda.model.EmailType;
import cz.kyndryl.camunda.service.EmailService;
import io.camunda.client.annotation.CustomHeaders;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendEmailWorker {

    private final EmailService emailService;

    @JobWorker(type = "jprdemo_com.kyndryl.camunda:sendEmail:1")
    public void sendEmail(final ActivatedJob job, @CustomHeaders Map<String, String> headers) {
        final Map<String, Object> variables = job.getVariablesAsMap();
        final EmailType emailType = EmailType.valueOf(headers.get("emailType"));

        log.info("SendEmail worker triggered: emailType={}, processInstance={}",
                emailType, job.getProcessInstanceKey());

        switch (emailType) {
            case POSITION_REJECTED -> sendPositionRejectedEmail(variables);
        }
    }

    /** Prefers the real requester email from the specification form; falls back to the legacy name-based address. */
    private static String requesterAddress(Map<String, Object> vars) {
        final Object email = vars.get("requesterEmail");
        return email instanceof String s && !s.isBlank() ? s : vars.get("requester") + "@company.com";
    }

    // ---- Per-type email builders --------------------------------------------------------

    private void sendPositionRejectedEmail(Map<String, Object> vars) {
        final String requester = (String) vars.get("requester");
        final String title = (String) vars.get("title");
        final String comment = (String) vars.get("approvalComment");

        emailService.sendEmail(
                requesterAddress(vars),
                "Job Position Rejected: " + title,
                "Your job position request \"" + title + "\" has been rejected.\n\n"
                        + (comment != null ? "Reason: " + comment : "No reason provided.")
        );
    }



}
