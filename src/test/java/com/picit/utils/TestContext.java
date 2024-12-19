package com.picit.utils;

import org.springframework.stereotype.Component;

@Component
public class TestContext {

    private String jwtToken;
    private String userId;
    private String postId;

    public String getJwtToken() {
        return jwtToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
