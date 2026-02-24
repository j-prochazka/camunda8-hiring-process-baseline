package cz.kyndryl.camunda.service;

/**
 * Service for sending messages to JMS queues and topics.
 * Works with both embedded Artemis and external message brokers.
 */
public interface MessageService {

    /**
     * Send a message to a JMS queue.
     */
    void sendMessage(String queueName, String message);

    /**
     * Publish a message to a JMS topic.
     */
    void publishToTopic(String topicName, String message);
}
