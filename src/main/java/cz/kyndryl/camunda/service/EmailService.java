package cz.kyndryl.camunda.service;

/**
 * Generic email sending service.
 * <p>
 * This interface has no knowledge of Camunda, process variables, or email types.
 * Callers (e.g. job workers) are responsible for resolving the recipient, subject
 * and body before invoking this service.
 */
public interface EmailService {

    /**
     * Send a plain email.
     *
     * @param to      the recipient email address
     * @param subject the email subject line
     * @param body    the plain-text email body
     */
    void sendEmail(String to, String subject, String body);
}
