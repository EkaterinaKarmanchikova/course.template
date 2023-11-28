package ru.template.example.documents.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Ответ от Kafka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    @NotNull
    private Long id;
    @NotNull
    private String status;
}
