package com.tidyquestion.backend.doc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class TextChunkerTest {

    @Test
    void shouldSplitTextWithOverlap() {
        TextChunker chunker = new TextChunker(10, 2);
        List<String> chunks = chunker.split("abcdefghijklmno");

        Assertions.assertEquals(2, chunks.size());
        Assertions.assertEquals("abcdefghij", chunks.get(0));
        Assertions.assertEquals("ijklmno", chunks.get(1));
    }
}
