package ru.template.example.documents.exception;

/**
 * Исключение если документ не найден
 */
public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}