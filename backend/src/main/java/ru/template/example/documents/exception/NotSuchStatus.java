package ru.template.example.documents.exception;

/**
 * Исключение если не найден в БД
 */
public class NotSuchStatus extends RuntimeException {
    public NotSuchStatus(String message) {
        super(message);
    }
}