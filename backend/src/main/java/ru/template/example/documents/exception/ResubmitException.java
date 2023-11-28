package ru.template.example.documents.exception;

/**
 * Исключение при повторной отправки документа
 */
public class ResubmitException extends RuntimeException {
    public ResubmitException(String message) {
        super(message);
    }
}