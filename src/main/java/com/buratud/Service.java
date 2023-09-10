package com.buratud;

import java.io.IOException;

import com.buratud.services.ChatGPT;
import com.buratud.services.ComputerVision;

public class Service {
    private static volatile Service instance;
    public ChatGPT chatgpt;
    public ComputerVision vision;

    private Service() throws IOException {
        chatgpt = new ChatGPT(Env.OPENAI_API_KEY);
        vision = new ComputerVision(Env.AZURE_VISION_ENDPOINT, Env.AZURE_VISION_KEY);

    }

    public static Service getInstance() throws IOException {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new Service();
                }
            }
        }
        return instance;
    }
}
