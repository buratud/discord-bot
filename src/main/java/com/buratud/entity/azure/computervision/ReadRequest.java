package com.buratud.entity.azure.computervision;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ReadRequest(String url) {
}
