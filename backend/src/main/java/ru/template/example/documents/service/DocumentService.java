package ru.template.example.documents.service;

import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.controller.dto.AnswerDto;

import java.util.List;
import java.util.Set;

/**
 * Сервис по работе с документами
 */
public interface DocumentService {
    /**
     * Сохранение документа
     * @param documentDto документ
     * @return сохраненный документ
     */
    DocumentDto save(DocumentDto documentDto);

    /**
     * Удаление документов
     * @param ids номер документов
     */
    void deleteAll(Set<Long> ids);

    /**
     * Удалиние документа по номеру
     * @param id номер документа
     */
    void delete(Long id);

    /**
     * Обновление документа
     * @param documentDto документ
     * @return обновленный документ
     */
    DocumentDto update(DocumentDto documentDto);

    /**
     * Получение всех документов
     * @return список документов
     */
    List<DocumentDto> findAll();

    /**
     * Получение документа по номеру
     * @param id номер
     * @return документ
     */
    DocumentDto get(Long id);
    /**
     * Отправка документ на подтверждение
     *
     * @param id номер документа
     * @return документ
     */
    DocumentDto sendOnApprove(Long id);

    void updateFromKafkaMessage(AnswerDto answerDto);
}
