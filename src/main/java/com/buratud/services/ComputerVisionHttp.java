package com.buratud.services;

import com.buratud.Utility;
import com.buratud.entity.azure.computervision.ReadRequest;
import com.buratud.entity.azure.computervision.ReadResponseBody;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Optional;

public class ComputerVisionHttp {
    private final String readUrl;
    private final String key;
    private final HttpClient httpClient;

    public ComputerVisionHttp(String endpoint, String key) {
        this.key = key;
        this.readUrl = String.format("%s/vision/v3.2/read/analyze", endpoint);
        httpClient = HttpClient.newHttpClient();
    }

    public String Read(String url) throws IOException, InterruptedException {
        ReadRequest body = new ReadRequest(url);
        String bodyString = Utility.mapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(readUrl))
                .header("Content-Type", "application/json")
                .header("Ocp-Apim-Subscription-Key", key)
                .method("POST", HttpRequest.BodyPublishers.ofString(bodyString))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Optional<String> taskUrl = response.headers().firstValue("Operation-Location");
        return taskUrl.orElse(null);
    }

    public String[] ReadResult(String taskUrl) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(taskUrl))
                .header("Ocp-Apim-Subscription-Key", key)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        boolean ok = false;
        ReadResponseBody result = null;
        HttpResponse<String> response;
        while (!ok) {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            result = Utility.mapper.readValue(response.body(), ReadResponseBody.class);
            ok = result.status.equals("succeeded") || result.status.equals("failed");
            Thread.sleep(2000);
        }
        return Arrays.stream(result.analyzeResult.readResults)
                .flatMap(read -> Arrays.stream(read.lines))
                .map(line -> line.text)
                .toArray(String[]::new);
    }
}
