package io.legacyfighter.cabs.contracts.application.editor;

import java.util.UUID;

public class CommitResult {


    public enum Result{
        FAILURE, SUCCESS
    }

    private UUID contentId;
    private Result result;
    private String message;

    public CommitResult(UUID contentId, Result result, String message) {
        this.contentId = contentId;
        this.result = result;
        this.message = message;
    }

    public CommitResult(UUID documentId, Result result) {
        this(documentId, result, null);
    }

    public Result getResult() {
        return result;
    }

    public UUID getContentId() {
        return contentId;
    }
}
