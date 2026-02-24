package cz.kyndryl.camunda.config;

import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.apache.activemq.artemis.jms.client.ActiveMQTopic;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;

@Slf4j
@Configuration
@Profile("embedded")
public class ArtemisConfig {

    /**
     * Makes the embedded broker usable out of the box:
     * <ul>
     *   <li>queues and addresses are auto-created, so {@code MessageService.sendMessage("any-queue", ...)} works</li>
     *   <li>the hiring portal topic gets a durable subscription queue ({@code <topic>.archive}), otherwise
     *       published messages have no subscriber, are silently dropped and are invisible in the Artemis Monitor</li>
     *   <li>the disk usage check is disabled - otherwise the broker blocks producers on a nearly full disk</li>
     * </ul>
     */
    @Bean
    public ArtemisConfigurationCustomizer embeddedBrokerCustomizer(
            @Value("${app.jms.topic.hiring-portal}") String topicName) {
        return configuration -> {
            configuration.addAddressSetting("#", new AddressSettings()
                    .setAutoCreateQueues(true)
                    .setAutoCreateAddresses(true)
                    .setDefaultAddressRoutingType(RoutingType.ANYCAST)
                    .setDefaultQueueRoutingType(RoutingType.ANYCAST));
            configuration.setMaxDiskUsage(-1);
            try {
                configuration.addQueueConfiguration(QueueConfiguration.of(topicName + ".archive")
                        .setAddress(topicName)
                        .setRoutingType(RoutingType.MULTICAST)
                        .setDurable(true));
            } catch (Exception e) {
                throw new IllegalStateException("Cannot declare queue for topic " + topicName, e);
            }
        };
    }

    @Bean
    public ActiveMQTopic hiringPortalTopic(
            @Value("${app.jms.topic.hiring-portal}") String topicName) {
        log.info("Creating JMS topic: {}", topicName);
        return new ActiveMQTopic(topicName);
    }

    /**
     * Point-to-point template used by {@code MessageService.sendMessage}. It has to be declared explicitly:
     * Spring Boot backs off its own JmsTemplate as soon as another one exists, and the topic template below
     * would then silently be used for "queues" too (messages published to a topic without subscribers are lost).
     */
    @Bean
    @Primary
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new JmsTemplate(connectionFactory);
    }

    @Bean
    public JmsTemplate topicJmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

    @Bean
    public JmsListenerContainerFactory<?> topicListenerFactory(
            ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }
}
