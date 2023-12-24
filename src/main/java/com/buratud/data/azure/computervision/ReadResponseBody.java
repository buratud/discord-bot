package com.buratud.data.azure.computervision;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReadResponseBody {
    public String status;
    public AnalyzeResult analyzeResult;
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AnalyzeResult {
        public ReadResult[] readResults;
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ReadResult {
            public Line[] lines;
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Line {
                public String text;
            }
        }
    }
}
