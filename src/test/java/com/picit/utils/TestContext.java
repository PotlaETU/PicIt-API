package com.picit.utils;

import org.springframework.stereotype.Component;

@Component
public class TestContext {

    private String jwtToken;

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
