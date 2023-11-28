package ru.template.example.documents.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.template.example.documents.entity.OutboxDocumentEntity;

import java.util.List;


public interface OutboxDocumentRepository extends JpaRepository<OutboxDocumentEntity, Long> {
    List<OutboxDocumentEntity> findBySendFalse();

    @Transactional
    @Modifying
    @Query("update OutboxDocumentEntity d set d.accepted = ?1 where d.id = ?2")
    void updateAcceptedById(boolean accepted, Long id);
}
