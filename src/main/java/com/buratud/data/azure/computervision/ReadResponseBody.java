package com.buratud.data.azure.computervision;

public class ReadResponseBody {
    public String status;
    public AnalyzeResult analyzeResult;
    public static class AnalyzeResult {
        public ReadResult[] readResults;
        public static class ReadResult {
            public Line[] lines;
            public static class Line {
                public String text;
            }
        }
    }
}
