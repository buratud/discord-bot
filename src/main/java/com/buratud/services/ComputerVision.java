package com.buratud.services;

import java.io.IOException;

public class ComputerVision {
    private final ComputerVisionHttp client;

    public ComputerVision(String endpoint, String key) {
        this.client = new ComputerVisionHttp(endpoint, key);
    }

    public String[] extractText(String url) throws IOException, InterruptedException {
        String taskUrl = client.Read(url);
        if (taskUrl == null) {
            throw new IOException("Failed to create OCR");
        }
        return client.ReadResult(taskUrl);
    }
}
