package com.tidyquestion.backend.rag;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 向量工具：
 * 1. 对齐固定维度，避免数据库维度不一致报错；
 * 2. 序列化为 pgvector 文本格式。
 */
public final class VectorUtils {

    private VectorUtils() {
    }

    public static List<Double> normalizeDimension(List<Double> source, int targetDimension) {
        List<Double> normalized = new ArrayList<>(targetDimension);
        for (int i = 0; i < targetDimension; i++) {
            double value = i < source.size() ? source.get(i) : 0D;
            normalized.add(value);
        }
        return normalized;
    }

    public static String toPgVectorLiteral(List<Double> embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.size(); i++) {
            double v = embedding.get(i);
            BigDecimal rounded = BigDecimal.valueOf(v).setScale(8, RoundingMode.HALF_UP);
            sb.append(rounded.toPlainString());
            if (i < embedding.size() - 1) {
                sb.append(',');
            }
        }
        sb.append(']');
        return sb.toString();
    }
}
