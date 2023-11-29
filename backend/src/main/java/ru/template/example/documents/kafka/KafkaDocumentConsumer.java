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
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.entity.OutboxMessageEntity;
import ru.template.example.documents.repository.OutboxMessageRepository;


import java.util.UUID;

/**
 * Компонент для приёма сообщений
 */
@Component
@AllArgsConstructor
public class KafkaDocumentConsumer {
    private final ObjectMapper objectMapper;
    private final OutboxMessageRepository outboxMessageRepository;

    /**
     * Метод отслеживает поступающие сообщения
     *
     * @param message текст сообщения
     * @param key     ключ сообщения
     * @throws JsonProcessingException при получении некорректного объекта
     */
    @KafkaListener(topics = "documents", groupId = "group_id")
    public void consumer(@Payload String message,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) throws JsonProcessingException {
        DocumentDto documentDto = objectMapper.readValue(message, DocumentDto.class);
        checkMessageFromKafkaAnswer(Long.decode(key));
        AnswerDto answerDto = new AnswerDto(documentDto.getId(), "ACCEPTED");
        OutboxMessageEntity messageForKafkaAnswer
                = prepareMessageEntity(Long.decode(key), answerDto);
        outboxMessageRepository.save(messageForKafkaAnswer);
    }

    /**
     * Проверка на получение сообщения
     *
     * @param id уникальный ключ сообщения
     */
    private void checkMessageFromKafkaAnswer(Long id) {
        var message = outboxMessageRepository.findById(id);
        if (message.isPresent()) {
            throw new KafkaException("Problem with message");
        }
    }

    /**
     * Подготовка записи для ответного сообщения
     *
     * @param id        идентификатор сообщения
     * @param answerDto объект для передачи через сообщение
     * @return сообщения для записи в БД
     */
    private OutboxMessageEntity prepareMessageEntity(Long id, AnswerDto answerDto) {
        OutboxMessageEntity messageForKafkaAnswer = new OutboxMessageEntity();
        messageForKafkaAnswer.setId(id);
        messageForKafkaAnswer.setSend(false);
        try {
            String json = objectMapper.writeValueAsString(answerDto);
            messageForKafkaAnswer.setMessage(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return messageForKafkaAnswer;
    }
}