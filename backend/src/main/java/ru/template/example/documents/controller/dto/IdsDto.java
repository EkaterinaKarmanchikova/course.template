package ru.template.example.documents.controller.dto;

import lombok.Data;

import java.util.Set;
import javax.validation.constraints.NotEmpty;

@Data
public class IdsDto {
    @NotEmpty
    private Set<Long> ids;
}
