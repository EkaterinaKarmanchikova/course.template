package ru.template.example.documents.kafka;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.template.example.documents.entity.OutboxMessageEntity;
import ru.template.example.documents.repository.OutboxMessageRepository;


import java.util.List;

/**
 * Реализация переодичной отправки сообщений с ответами
 */
@Component
@AllArgsConstructor
public class OutboxAnswerTask {
    private static final long TIME_RATE = 1000;
    private static final String KAFKA_TOPIC_NAME = "answer";
    private final KafkaSender kafkaSender;
    private final OutboxMessageRepository outboxMessageRepository;

    @Scheduled(fixedRate = TIME_RATE)
    public void sendMessagesToKafka() {
        List<OutboxMessageEntity> messages = outboxMessageRepository.findBySendFalse();
        messages.forEach((message) -> {
            kafkaSender.sendMessage(message.getMessage(), message.getId().toString(), KAFKA_TOPIC_NAME);
            message.setSend(true);
            outboxMessageRepository.save(message);
        });
    }
}
