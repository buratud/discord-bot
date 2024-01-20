package com.buratud.entity.openai.chat;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatCompletionStreamResponse {
    @JsonProperty("id")
    public String id;

    @JsonProperty("object")
    public String object;

    @JsonProperty("created")
    public long created;

    @JsonProperty("model")
    public String model;

    @JsonProperty("system_fingerprint")
    public String systemFingerprint;

    @JsonProperty("choices")
    public Choice[] choices;

    @JsonProperty("usage")
    public Usage usage;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        @JsonProperty("index")
        public int index;

        @JsonProperty("delta")
        public Message delta;

        @JsonProperty("finish_reason")
        public String finishReason;
    }

    public static class Message {
        @JsonProperty("role")
        public String role;

        @JsonProperty("content")
        public String content;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {
        @JsonProperty("prompt_tokens")
        public int promptTokens;

        @JsonProperty("completion_tokens")
        public int completionTokens;

        @JsonProperty("total_tokens")
        public int totalTokens;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
