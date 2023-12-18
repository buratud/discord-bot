package com.buratud;

import com.buratud.interactions.Attendance;
import com.buratud.services.AttendanceService;
import com.buratud.services.ChatGpt;
import com.buratud.services.ComputerVision;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Service {
    private static final Logger logger = LogManager.getLogger(Service.class);
    private static volatile Service instance;
    public ChatGpt chatgpt;
    public ComputerVision vision;
    public AttendanceService attendance;

    private Service() throws IOException {
        if (Env.OPENAI_API_KEY != null) {
            logger.info("ChatGPT activated");
            chatgpt = new ChatGpt(Env.OPENAI_API_KEY);
        }
        logger.info("Computer vision activated");
        vision = new ComputerVision(Env.AZURE_VISION_ENDPOINT, Env.AZURE_VISION_KEY);
        attendance = new AttendanceService();
    }

    public static synchronized Service getInstance() throws IOException {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }
}
