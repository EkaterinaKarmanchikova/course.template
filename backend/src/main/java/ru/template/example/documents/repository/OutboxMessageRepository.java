package ru.template.example.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.template.example.documents.entity.OutboxMessageEntity;

import java.util.List;
import java.util.Optional;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessageEntity, Long> {
    List<OutboxMessageEntity> findBySendFalse();

    Optional<OutboxMessageEntity> findById(Long id);
}
