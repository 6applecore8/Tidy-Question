CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE IF NOT EXISTS knowledge_document (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(120),
    storage_path TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    chunk_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS document_chunk (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL REFERENCES knowledge_document(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,
    chunk_index INT NOT NULL,
    content TEXT NOT NULL,
    embedding VECTOR(1536) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_document_user_created ON knowledge_document(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_chunk_doc_idx ON document_chunk(document_id, chunk_index);
CREATE INDEX IF NOT EXISTS idx_chunk_embedding_cosine ON document_chunk USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

CREATE TABLE IF NOT EXISTS quiz_session (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    topic VARCHAR(150) NOT NULL,
    difficulty VARCHAR(40) NOT NULL,
    current_question TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS quiz_answer (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL REFERENCES quiz_session(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL,
    question TEXT NOT NULL,
    user_answer TEXT NOT NULL,
    llm_score INT NOT NULL,
    rule_score INT NOT NULL,
    final_score INT NOT NULL,
    strengths TEXT,
    improvements TEXT,
    ideal_answer TEXT,
    key_points TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_quiz_answer_user_score ON quiz_answer(user_id, final_score, created_at DESC);
