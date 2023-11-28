package ru.template.example.detail;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ведения логов
 */
@Component
@Aspect
public class LoggingDetail {
    private final Logger logger = Logger.getLogger(LoggingDetail.class.getName());

    /**
     * Точка соединения для контроллеров
     */
    @Pointcut("within(ru.template.example.documents.controller.*)")
    public void controllerPointcut() {
    }

    /**
     * Запись в журнал после выполнения функции в контроллере
     *
     * @param joinPoint точка наблюдения контроллера
     */
    @After("controllerPointcut()")
    public void logInfoMethodCall(JoinPoint joinPoint) {
        String name = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        logger.log(Level.INFO, "Пришел запрос " + name + "\nАргументы в запросе " + List.of(args));
    }
}
