package com.buratud.data.openai.chat;

import com.google.gson.annotations.SerializedName;

public class ChatCompletionStreamResponse {
    @SerializedName("id")
    public String id;

    @SerializedName("object")
    public String object;

    @SerializedName("created")
    public long created;

    @SerializedName("model")
    public String model;

    @SerializedName("system_fingerprint")
    public String systemFingerprint;

    @SerializedName("choices")
    public Choice[] choices;

    @SerializedName("usage")
    public Usage usage;

    public static class Choice {
        @SerializedName("index")
        public int index;

        @SerializedName("delta")
        public Message delta;

        @SerializedName("finish_reason")
        public String finishReason;
    }

    public static class Message {
        @SerializedName("role")
        public String role;

        @SerializedName("content")
        public String content;
    }

    public static class Usage {
        @SerializedName("prompt_tokens")
        public int promptTokens;

        @SerializedName("completion_tokens")
        public int completionTokens;

        @SerializedName("total_tokens")
        public int totalTokens;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
