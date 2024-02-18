package com.buratud.entity.googleai;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE
)
public interface Part {

    @JsonCreator
    static Part create(
            @JsonProperty("text") String text,
            @JsonProperty("inline_data") InlineDataPart inlineDataPart
    ) {
        if (text != null) {
            return new TextPart(text);
        } else if (inlineDataPart != null) {
            return inlineDataPart;
        } else {
            throw new IllegalArgumentException("Invalid Part object");
        }
    }
}
