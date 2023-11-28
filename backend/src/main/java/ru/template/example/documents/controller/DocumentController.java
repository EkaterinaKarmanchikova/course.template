package ru.template.example.documents.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.template.example.documents.controller.dto.DocumentDto;
import ru.template.example.documents.controller.dto.IdDto;
import ru.template.example.documents.controller.dto.IdsDto;
import ru.template.example.documents.service.DocumentService;

import javax.validation.Valid;
import java.util.List;

/**
 * Рест-контроллер для обработки запросов
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService service;

    /**
     * Пост запрос для записи документа
     *
     * @param dto документ из запроса
     * @return документ
     */
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto save(@Valid @RequestBody DocumentDto dto) {
        return service.save(dto);
    }

    /**
     * Получение свиска документов
     *
     * @return список документов
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DocumentDto> get() {
        return service.findAll();
    }

    /**
     * Отправка документа на обработку
     *
     * @param id номер документа
     * @return документ отправленный на обработку
     */
    @PostMapping(
            path = "send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public DocumentDto send(@Valid @RequestBody IdDto id) {

        return service.sendOnApprove(id.getId());
    }

    /**
     * Удаление документа по полученному номеру
     *
     * @param id номер документа
     */
    @DeleteMapping(path = "/{id}")
    public void delete(@Valid @PathVariable Long id) {
        service.delete(id);
    }

    /**
     * Удаление нескольких документов
     *
     * @param idsDto список номеров
     */
    @DeleteMapping
    public void deleteAll(@Valid @RequestBody IdsDto idsDto) {
        service.deleteAll(idsDto.getIds());
    }

}
