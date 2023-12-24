package com.buratud.googleai;

public enum Role {
    SYSTEM("system"),
    USER("user"),
    MODEL("model");

    private String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
}
