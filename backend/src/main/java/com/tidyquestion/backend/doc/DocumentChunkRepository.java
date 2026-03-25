package com.tidyquestion.backend.doc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档分块数据访问层。
 */
@Repository
public class DocumentChunkRepository {

    private static final RowMapper<ChunkRecord> CHUNK_ROW_MAPPER = (rs, rowNum) -> new ChunkRecord(
            rs.getLong("id"),
            rs.getLong("document_id"),
            rs.getInt("chunk_index"),
            rs.getString("content"),
            rs.getDouble("score")
    );

    private final JdbcTemplate jdbcTemplate;

    public DocumentChunkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void deleteByDocumentId(Long documentId) {
        jdbcTemplate.update("DELETE FROM document_chunk WHERE document_id = ?", documentId);
    }

    /**
     * 批量保存分块和向量。
     *
     * embedding 使用 pgvector 文本格式（例如 [0.1,0.2,...]）插入。
     */
    public void batchInsert(Long documentId, Long userId, List<String> chunks, List<String> embeddingVectors) {
        List<Object[]> args = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            args.add(new Object[]{documentId, userId, i, chunks.get(i), embeddingVectors.get(i)});
        }

        jdbcTemplate.batchUpdate(
                """
                        INSERT INTO document_chunk(document_id, user_id, chunk_index, content, embedding)
                        VALUES (?, ?, ?, ?, CAST(? AS vector))
                        """,
                args
        );
    }

    /**
     * 余弦距离检索，score 越小代表越相似。
     */
    public List<ChunkRecord> searchSimilar(Long userId, String queryVector, int topK) {
        return jdbcTemplate.query(
                """
                        SELECT id, document_id, chunk_index, content,
                               embedding <=> CAST(? AS vector) AS score
                        FROM document_chunk
                        WHERE user_id = ?
                        ORDER BY score ASC
                        LIMIT ?
                        """,
                CHUNK_ROW_MAPPER,
                queryVector,
                userId,
                topK
        );
    }
}
