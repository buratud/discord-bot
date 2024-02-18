package com.buratud.entity.ai;

import com.buratud.entity.openai.chat.Role;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class RoleSerializer extends JsonSerializer<com.buratud.entity.openai.chat.Role> {

    @Override
    public void serialize(Role role,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeString(role.getRole());
    }
}
