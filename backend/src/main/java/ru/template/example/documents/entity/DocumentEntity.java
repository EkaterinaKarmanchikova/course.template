package ru.template.example.documents.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.Instant;

/**
 * Сущность "Документ"
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "document")
public class DocumentEntity {
    /**
     * Номер документа
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /**
     * Тип документа
     */
    @Column(name = "type", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String type;

    /**
     * Организация
     */
    @Column(name = "organization", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String organization;

    /**
     * Дата
     */
    @Column(name = "date")
    private Instant date;

    /**
     * ФИО пациента
     */
    @Column(name = "patient", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String patient;

    @Column(name = "description")
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    /**
     * Комментарий
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private StatusEntity status;
}