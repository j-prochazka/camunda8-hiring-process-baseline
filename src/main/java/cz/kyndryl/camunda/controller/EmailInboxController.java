package cz.kyndryl.camunda.controller;

import com.icegreen.greenmail.util.GreenMail;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Profile("embedded")
@RestController
@RequestMapping("/api/emails")
@RequiredArgsConstructor
public class EmailInboxController {

    private final GreenMail greenMail;

    @GetMapping
    public List<Map<String, Object>> getEmails() {
        final MimeMessage[] messages = greenMail.getReceivedMessages();
        final List<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < messages.length; i++) {
            final MimeMessage msg = messages[i];
            final Map<String, Object> email = new LinkedHashMap<>();
            try {
                email.put("id", i);
                email.put("subject", msg.getSubject());
                email.put("from", addressesToString(msg.getFrom()));
                email.put("to", addressesToString(msg.getAllRecipients()));
                email.put("sentDate", msg.getSentDate() != null
                        ? msg.getSentDate().toInstant().atOffset(ZoneOffset.UTC).toString()
                        : null);
                email.put("body", extractBody(msg));
            } catch (MessagingException e) {
                log.warn("Could not parse email at index {}", i, e);
            }
            result.add(email);
        }

        Collections.reverse(result); // newest first
        return result;
    }

    private String addressesToString(Address[] addresses) {
        if (addresses == null) return "";
        return Arrays.stream(addresses)
                .map(Address::toString)
                .collect(Collectors.joining(", "));
    }

    private String extractBody(MimeMessage msg) {
        try {
            final Object content = msg.getContent();
            return content != null ? content.toString() : "";
        } catch (IOException | MessagingException e) {
            return "(Unable to read body)";
        }
    }
}
