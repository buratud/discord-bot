package com.buratud;

public class Env {
    public static String DISCORD_TOKEN = System.getenv("DISCORD_TOKEN");
    public static String AZURE_VISION_ENDPOINT = System.getenv("AZURE_VISION_ENDPOINT");
    public static String AZURE_VISION_KEY = System.getenv("AZURE_VISION_KEY");
    public static String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    public static String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
    public static String AWS_REGION = System.getenv("AWS_REGION");
    public static String AWS_ACCESS_KEY = System.getenv("AWS_ACCESS_KEY");
    public static String AWS_SECRET_ACCESS_KEY = System.getenv("AWS_SECRET_ACCESS_KEY");

}
