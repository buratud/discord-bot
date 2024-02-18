package com.buratud.entity.googleai;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TextPart implements Part {
    private String text;

    public TextPart(String text) {
        this.text = text;
    }
}
