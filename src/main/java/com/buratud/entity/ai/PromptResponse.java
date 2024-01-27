package com.buratud.entity.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class PromptResponse {
    public PromptResponse(boolean flagged, String message, FinishReason finishReason) {
        this.flagged = flagged;
        this.message = message;
        this.finishReason = finishReason;
    }
    @Setter
    String chatId;
    boolean flagged;
    String message;
    FinishReason finishReason;
}
