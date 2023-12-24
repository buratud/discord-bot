package com.buratud.data.googleai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionStreamResponse {
    private Candidate[] candidates;
    private PromptFeedback promptFeedback;

    @Getter
    @Setter
    public static class Candidate {
        private Content content;
        private String finishReason;
        private int index;
        private SafetyRating[] safetyRatings;
    }

    @Getter
    @Setter
    public static class Content {
        private Part[] parts;
        private String role;
    }

    @Getter
    @Setter
    public static class Part {
        private String text;
    }

    @Getter
    @Setter
    public static class SafetyRating {
        private String category;
        private String probability;
    }

    @Getter
    @Setter
    public static class PromptFeedback {
        private String blockReason;
        private SafetyRating[] safetyRatings;
    }
}