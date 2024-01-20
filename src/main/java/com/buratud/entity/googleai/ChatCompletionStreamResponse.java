package com.buratud.entity.googleai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;
        private String finishReason;
        private int index;
        private SafetyRating[] safetyRatings;
        private CitationMetadata citationMetadata;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private Part[] parts;
        private String role;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SafetyRating {
        private String category;
        private String probability;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PromptFeedback {
        private String blockReason;
        private SafetyRating[] safetyRatings;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CitationMetadata {
        private CiatationSource[] citationSources;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CiatationSource {
        private int startIndex;
        private int endIndex;
        private String uri;
        private String license;
    }
}