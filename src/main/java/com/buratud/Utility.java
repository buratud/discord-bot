package com.buratud;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Utility {
    public static final ObjectMapper mapper;
    static {
        mapper  = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }
}
