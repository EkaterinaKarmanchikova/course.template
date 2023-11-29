package ru.template.example.documents.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.template.example.documents.controller.dto.AnswerDto;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.entity.DocumentEntity;
import ru.template.example.documents.entity.OutboxDocumentEntity;
import ru.template.example.documents.entity.StatusEntity;
import ru.template.example.documents.exception.NotFoundException;
import ru.template.example.documents.exception.NotSuchStatus;
import ru.template.example.documents.exception.ResubmitException;
import ru.template.example.documents.repository.DocumentRepository;
import ru.template.example.documents.repository.OutboxDocumentRepository;
import ru.template.example.documents.repository.StatusRepository;


import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final StatusRepository statusRepository;
    private final OutboxDocumentRepository outboxDocumentRepository;
    private final ObjectMapper objectMapper;
    private final MapperFacade mapperFacade = new DefaultMapperFactory
            .Builder()
            .build()
            .getMapperFacade();

    /**
     * Сохранение документа
     *
     * @param documentDto документ
     * @return документ
     */
    @Transactional
    public DocumentDto save(DocumentDto documentDto) {
        DocumentEntity document = mapperFacade.map(documentDto, DocumentEntity.class);
        StatusEntity status = getStatusForDocumentByName("NEW");
        document.setStatus(status);
        document.setDate(Instant.now());
        documentRepository.save(document);
        return mapperFacade.map(document, DocumentDto.class);
    }

    /**
     * Обновление документ
     *
     * @param documentDto документ
     * @return документ
     */
    @Transactional
    public DocumentDto update(DocumentDto documentDto) {
        Optional<DocumentEntity> documentEntityOptional = documentRepository.findById(documentDto.getId());
        checkDocument(documentEntityOptional);
        StatusEntity status = getStatusForDocumentByName(documentDto.getStatus().getCode());
        documentRepository.updateStatusById(status, documentDto.getId());
        return documentDto;
    }

    /**
     * Отправка документа на подтверждение
     *
     * @param id номер документа
     * @return документ
     */
    @Transactional
    public DocumentDto sendOnApprove(Long id) {
        Optional<DocumentEntity> documentEntityOptional = documentRepository.findById(id);
        if (!documentEntityOptional
                .orElseThrow(() -> new NotFoundException("The document is not in the database"))
                .getStatus()
                .getCode()
                .equals("NEW")) {
            throw new ResubmitException("The document has already been sent");
        }
        DocumentDto documentDto = get(id);
        StatusEntity status = getStatusForDocumentByName("IN_PROCESS");
        addToTableForKafkaSender(documentDto);
        documentRepository.updateStatusById(status, id);
        return documentDto;
    }

    /**
     * Удаление одного документа по номеру
     *
     * @param id номер документа
     */
    @Transactional
    public void delete(Long id) {
        documentRepository.deleteById(id);
    }

    /**
     * Удаление нескольких документов
     *
     * @param ids номера документов
     */
    @Transactional
    public void deleteAll(Set<Long> ids) {
        ids.forEach(this::delete);
    }

    /**
     * Получение списка документов
     *
     * @return список документов
     */
    public List<DocumentDto> findAll() {
        List<DocumentEntity> documents
                = documentRepository.findAll();
        return mapperFacade.mapAsList(documents, DocumentDto.class);
    }

    /**
     * Получение документа по номеру
     *
     * @param id номер документа
     * @return документ
     */
    public DocumentDto get(Long id) {
        Optional<DocumentEntity> document = documentRepository.findById(id);
        return mapperFacade.map(document.orElseThrow(() -> new NotFoundException("Документ отсутствует в базе")), DocumentDto.class);
    }

    @Transactional
    public void updateFromKafkaMessage(AnswerDto answerDto) {
        StatusEntity status = getStatusForDocumentByName(answerDto.getStatus());
        Optional<DocumentEntity> document = documentRepository.findById(answerDto.getId());
        document.orElseThrow(() -> new NotFoundException("Документ отсутствует в базе"))
                .setStatus(status);
        document.ifPresent(documentRepository::save);

    }

    /**
     * Поиск статус по названию
     *
     * @param statusName - название статуса
     * @return статус
     */
    private StatusEntity getStatusForDocumentByName(String statusName) {
        Optional<StatusEntity> status = statusRepository.findByCode(statusName);
        return status.orElseThrow(() -> new NotSuchStatus("Статус не найден"));
    }

    /**
     * Размещение сообщения с документом в таблице для последующей отправки сообщений
     *
     * @param documentDto документ
     */
    private void addToTableForKafkaSender(DocumentDto documentDto) {
        OutboxDocumentEntity message = new OutboxDocumentEntity();
        try {
            String json = objectMapper.writeValueAsString(documentDto);
            message.setMessage(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        message.setSend(false);
        message.setCreateDate(Instant.now());
        outboxDocumentRepository.save(message);
    }

    /**
     * Проверка на наличие документа
     *
     * @param document документ
     */
    private void checkDocument(Optional<DocumentEntity> document) {
        if (document.isEmpty()) {
            throw new NotFoundException("Документ отсутствует в базе");
        }
    }
}