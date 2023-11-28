package ru.template.example.documents.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Объект для отправки ответов на сообщения
 */
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "outbox_message")
public class OutboxMessageEntity {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "message", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String message;

    @Column(name = "send")
    private Boolean send;

    @Column(name = "accepted")
    private Boolean accepted;

    @Column(name = "create_date")
    private Instant createDate;
}