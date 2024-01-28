package com.buratud;

import com.buratud.services.AttendanceService;
import com.buratud.services.ComputerVision;
import com.buratud.services.GenerativeAi;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Service {
    private static final Logger logger = LogManager.getLogger(Service.class);
    private static volatile Service instance;
    public GenerativeAi generativeAi;
    public ComputerVision vision;
    public AttendanceService attendance;

    private Service() throws IOException {
        if (Env.OPENAI_API_KEY != null || Env.GEMINI_API_KEY != null) {
            generativeAi = new GenerativeAi();
            logger.info("AI activated");
        }
        if (Env.AZURE_VISION_ENDPOINT != null || Env.AZURE_VISION_KEY != null) {
            vision = new ComputerVision(Env.AZURE_VISION_ENDPOINT, Env.AZURE_VISION_KEY);
            logger.info("Computer vision activated");
        }
        attendance = new AttendanceService();
    }

    public static synchronized Service getInstance() throws IOException {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }
}
