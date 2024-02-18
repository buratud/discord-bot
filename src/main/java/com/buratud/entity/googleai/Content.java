package com.buratud.entity.googleai;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Content {
    @JsonSerialize(using = RoleSerializer.class)
    private Role role;
    private List<Part> parts;
}
