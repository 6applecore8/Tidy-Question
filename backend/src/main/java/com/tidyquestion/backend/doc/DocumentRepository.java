package com.tidyquestion.backend.doc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 文档数据访问层。
 */
@Repository
public class DocumentRepository {

    private static final RowMapper<DocumentRecord> DOC_ROW_MAPPER = (rs, rowNum) -> new DocumentRecord(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("file_name"),
            rs.getString("content_type"),
            rs.getString("storage_path"),
            rs.getString("status"),
            rs.getInt("chunk_count"),
            rs.getObject("created_at", OffsetDateTime.class),
            rs.getObject("updated_at", OffsetDateTime.class)
    );

    private final JdbcTemplate jdbcTemplate;

    public DocumentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 创建文档记录，初始状态通常是 UPLOADED。
     */
    public Long insert(Long userId, String fileName, String contentType, String storagePath, String status) {
        Long documentId = jdbcTemplate.queryForObject(
                """
                        INSERT INTO knowledge_document(user_id, file_name, content_type, storage_path, status)
                        VALUES (?, ?, ?, ?, ?)
                        RETURNING id
                        """,
                Long.class,
                userId,
                fileName,
                contentType,
                storagePath,
                status
        );
        if (documentId == null) {
            throw new IllegalStateException("Insert knowledge_document succeeded but no id returned");
        }
        return documentId;
    }

    public Optional<DocumentRecord> findById(Long id) {
        List<DocumentRecord> rows = jdbcTemplate.query(
                "SELECT * FROM knowledge_document WHERE id = ?",
                DOC_ROW_MAPPER,
                id
        );
        return rows.stream().findFirst();
    }

    /**
     * 更新文档状态与分块数量。
     */
    public void updateStatus(Long id, String status, Integer chunkCount) {
        jdbcTemplate.update(
                """
                        UPDATE knowledge_document
                        SET status = ?, chunk_count = ?, updated_at = NOW()
                        WHERE id = ?
                        """,
                status,
                chunkCount,
                id
        );
    }

    public List<DocumentRecord> listByUserId(Long userId) {
        return jdbcTemplate.query(
                "SELECT * FROM knowledge_document WHERE user_id = ? ORDER BY created_at DESC",
                DOC_ROW_MAPPER,
                userId
        );
    }
}
