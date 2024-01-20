package com.buratud.entity.openai.chat;

import java.util.List;

public class ChatCompletionRequestBuilder {

    private ChatCompletionRequest request;

    public ChatCompletionRequestBuilder(String model, List<ChatMessage> messages) {
        request = new ChatCompletionRequest();
        request.model = model;
        request.messages = messages;
    }

    public ChatCompletionRequestBuilder withTemperature(Float temperature) {
        request.temperature = temperature;
        return this;
    }

    public ChatCompletionRequestBuilder withTopP(Float topP) {
        request.topP = topP;
        return this;
    }

    public ChatCompletionRequestBuilder withN(Float n) {
        request.n = n;
        return this;
    }

    public ChatCompletionRequestBuilder withStream(Boolean stream) {
        request.stream = stream;
        return this;
    }

    public ChatCompletionRequestBuilder withMaxTokens(Integer maxTokens) {
        request.maxTokens = maxTokens;
        return this;
    }

    public ChatCompletionRequestBuilder withPresencePenalty(Integer presencePenalty) {
        request.presencePenalty = presencePenalty;
        return this;
    }

    public ChatCompletionRequestBuilder withFrequencyPenalty(Integer frequencyPenalty) {
        request.frequencyPenalty = frequencyPenalty;
        return this;
    }

    public ChatCompletionRequestBuilder withUser(String user) {
        request.user = user;
        return this;
    }

    public ChatCompletionRequest build() {
        return request;
    }
}