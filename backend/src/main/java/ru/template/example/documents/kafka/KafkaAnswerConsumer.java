package ru.template.example.documents.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.template.example.documents.controller.dto.AnswerDto;
import ru.template.example.documents.entity.OutboxDocumentEntity;
import ru.template.example.documents.repository.OutboxDocumentRepository;
import ru.template.example.documents.service.DocumentService;

import java.util.Optional;

/**
 * Компонент для получения ответов из Kafka
 */
@Component
@AllArgsConstructor
public class KafkaAnswerConsumer {
    private final ObjectMapper objectMapper;
    private final DocumentService documentService;
    private final OutboxDocumentRepository outboxDocumentRepository;

    /**
     * Метод отслеживает топик "ответ" и реагирует на полученные сообщения
     *
     * @param message текст сообщение
     * @param key     ключ сообщения
     * @throws JsonProcessingException при ошибках в тексте сообщения
     */
    @KafkaListener(topics = "answer", groupId = "group_id")
    public void consumer(@Payload String message,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) throws JsonProcessingException {
        AnswerDto answerDto = objectMapper.readValue(message, AnswerDto.class);
        Optional<OutboxDocumentEntity> messageForKafka
                = outboxDocumentRepository.findById(Long.getLong(key));
        if (messageForKafka.isEmpty() || messageForKafka.get().getAccepted()) {
            throw new KafkaException("Problem with message");
        }
        documentService.updateFromKafkaMessage(answerDto);
        outboxDocumentRepository.updateAcceptedById(true, messageForKafka.get().getId());
    }
}