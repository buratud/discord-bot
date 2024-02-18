package com.buratud.entity.googleai;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InlineDataPart implements Part {
    private InlineData inlineData;

    public InlineDataPart(InlineData inlineData) {
        this.inlineData = inlineData;
    }
}
