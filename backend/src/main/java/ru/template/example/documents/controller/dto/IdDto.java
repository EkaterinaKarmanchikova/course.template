package ru.template.example.documents.controller.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class IdDto {
    @NotNull
    @Min(1)
    private Long id;
}