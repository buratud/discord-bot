package com.buratud.data.googleai;

import com.buratud.Utility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletionRequest {
    private List<Content> contents;
    private List<SafetySetting> safetySettings;
    private GenerationConfig generationConfig;

    public String toJson() throws JsonProcessingException {
        return Utility.mapper.writeValueAsString(this);
    }
}

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
class Content {
    @JsonSerialize(using = RoleSerializer.class)
    private Role role;
    private List<Part> parts;
}

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
class Part {
    private String text;
}

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
class SafetySetting {
    private String category;
    private String threshold;
}

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
class GenerationConfig {
    private List<String> stopSequences;
    private double temperature;
    private int maxOutputTokens;
    private double topP;
    private int topK;
}
