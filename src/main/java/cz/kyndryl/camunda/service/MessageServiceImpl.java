package cz.kyndryl.camunda.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final JmsTemplate jmsTemplate;
    private final JmsTemplate topicJmsTemplate;

    public MessageServiceImpl(JmsTemplate jmsTemplate,
                              @Qualifier("topicJmsTemplate") JmsTemplate topicJmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.topicJmsTemplate = topicJmsTemplate;
    }

    @Override
    public void sendMessage(String queueName, String message) {
        try {
            jmsTemplate.convertAndSend(queueName, message);
            log.info("Message sent to queue '{}': {}", queueName, message);
        } catch (Exception e) {
            log.error("Failed to send message to queue '{}'", queueName, e);
            throw new RuntimeException("Failed to send message", e);
        }
    }

    @Override
    public void publishToTopic(String topicName, String message) {
        try {
            topicJmsTemplate.convertAndSend(topicName, message);
            log.info("Message published to topic '{}': {}", topicName, message);
        } catch (Exception e) {
            log.error("Failed to publish message to topic '{}'", topicName, e);
            throw new RuntimeException("Failed to publish message", e);
        }
    }
}
