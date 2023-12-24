package com.buratud.data.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public  class PromptResponse {
    boolean flagged;
    String message;
}
