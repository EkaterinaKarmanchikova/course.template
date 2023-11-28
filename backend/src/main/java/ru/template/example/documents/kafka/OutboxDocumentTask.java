package ru.template.example.documents.kafka;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.template.example.documents.entity.OutboxDocumentEntity;
import ru.template.example.documents.repository.OutboxDocumentRepository;

import java.util.List;

/**
 * Реализация переодической отправки сообщений с документами
 */
@Component
@AllArgsConstructor
public class OutboxDocumentTask {
    private static final long TIME_RATE = 1000;
    private static final String KAFKA_TOPIC_NAME = "documents";
    private final KafkaSender kafkaSender;
    private final OutboxDocumentRepository outboxDocumentRepository;

    @Scheduled(fixedRate = TIME_RATE)
    public void sendMessagesToKafka() {
        List<OutboxDocumentEntity> messages = outboxDocumentRepository.findBySendFalse();
        messages.forEach((message) -> {
            kafkaSender.sendMessage(message.getMessage(), message.getId().toString(), KAFKA_TOPIC_NAME);
            message.setSend(true);
            message.setAccepted(false);
            outboxDocumentRepository.save(message);
        });
    }
}
