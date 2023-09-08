package com.buratud.data.openai.chat;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class RoleSerializer implements JsonSerializer<Role> {
    @Override
    public JsonElement serialize(Role arg0, Type arg1, JsonSerializationContext arg2) {
        return new JsonPrimitive(arg0.getRole());
    }
}