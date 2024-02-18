package com.buratud.entity.googleai;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerationConfig {
    private List<String> stopSequences;
    private double temperature;
    private int maxOutputTokens;
    private double topP;
    private int topK;
}
