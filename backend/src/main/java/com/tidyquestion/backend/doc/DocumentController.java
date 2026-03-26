package com.tidyquestion.backend.doc;

import com.tidyquestion.backend.common.ApiResponse;
import com.tidyquestion.backend.doc.dto.DocumentContentResponse;
import com.tidyquestion.backend.doc.dto.DocumentIndexResponse;
import com.tidyquestion.backend.doc.dto.DocumentUploadResponse;
import com.tidyquestion.backend.doc.dto.DocumentView;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档管理接口。
 */
@Validated
@RestController
@RequestMapping("/api/docs")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * 上传文档。
     */
    @PostMapping("/upload")
    public ApiResponse<DocumentUploadResponse> upload(@RequestParam("file") MultipartFile file) {
        return ApiResponse.success(documentService.upload(file));
    }

    /**
     * 索引文档。
     */
    @PostMapping("/{docId}/index")
    public ApiResponse<DocumentIndexResponse> index(@PathVariable @Min(value = 1, message = "docId 不合法") Long docId) {
        return ApiResponse.success(documentService.index(docId));
    }

    /**
     * 文档列表。
     */
    @GetMapping
    public ApiResponse<List<DocumentView>> list() {
        return ApiResponse.success(documentService.list());
    }

    /**
     * 查看文档解析内容。
     */
    @GetMapping("/{docId}/content")
    public ApiResponse<DocumentContentResponse> content(
            @PathVariable @Min(value = 1, message = "docId 不合法") Long docId,
            @RequestParam(value = "maxChars", required = false)
            @Min(value = 200, message = "maxChars 必须 >= 200")
            @Max(value = 200000, message = "maxChars 必须 <= 200000") Integer maxChars) {
        return ApiResponse.success(documentService.content(docId, maxChars));
    }
}
