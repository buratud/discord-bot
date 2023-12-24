package com.buratud.googleai;

import com.buratud.data.openai.chat.ChatMessage;

import java.util.List;

public class ChatCompletionRequestBuilder {
    private ChatCompletionRequest request;
    public ChatCompletionRequestBuilder() {
        request = new ChatCompletionRequest();
    }
    public ChatCompletionRequestBuilder withContents(List<Content> contents) {
        this.request.setContents(contents);
        return this;
    }

    public ChatCompletionRequestBuilder withMessages(List<ChatMessage> messages) {
        List<Content> contents = ChatCompletionRequestMapper.INSTANCE.ChatMessageToContent(messages);
        if (contents.get(0).getRole() == Role.SYSTEM) {
            contents.remove(0);
        }
        this.request.setContents(contents);
        return this;
    }
    public ChatCompletionRequestBuilder withSafetySettings(List<SafetySetting> safetySettings) {
        this.request.setSafetySettings(safetySettings);
        return this;
    }
    public ChatCompletionRequestBuilder withGenerationConfig(GenerationConfig generationConfig) {
        this.request.setGenerationConfig(generationConfig);
        return this;
    }
    public ChatCompletionRequest build() {
        return this.request;
    }
}