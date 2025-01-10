package com.picit.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class TestContext {

    private static String jwtToken;
    private static String userId;
    private static String postId;

    public static String getJwtToken() {
        return jwtToken;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        TestContext.userId = userId;
    }

    public static String getPostId() {
        return postId;
    }

    public static void setPostId(String postId) {
        TestContext.postId = postId;
    }

    public void setJwtToken(String jwtToken) {
        TestContext.jwtToken = jwtToken;
    }

    public static <T> HttpEntity<T> setAuthHeaderWithBody(T body) {
        String token = getJwtToken();
        if (token == null) {
            throw new IllegalStateException("JWT token not found. Ensure the login step is executed first.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(body, headers);
    }

}
