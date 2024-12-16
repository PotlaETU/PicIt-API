package com.picit.stepdefs;

import com.picit.iam.dto.login.LoginRequest;
import com.picit.iam.dto.login.LoginResponse;
import com.picit.iam.dto.login.SignUpRequest;
import com.picit.utils.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IamStepDefs {

    @Autowired
    private TestContext testContext;

    private final String baseUrl = "http://localhost:8081/api/v1/iam";
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private ResponseEntity<LoginResponse> response;

    @When("^I create an account")
    public void iCreateAnAccount() {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email("test@test.com")
                .password("testTest")
                .username("test")
                .privacy(true)
                .notifications(true)
                .build();
        response = restTemplate.postForEntity(baseUrl + "/register", signUpRequest, LoginResponse.class);
        assertNotNull(response);
    }

    @Then("the account should be created")
    public void theAccountShouldBeCreated() {
        if (response.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("test", response.getBody().username());
    }

    @When("I login to the account")
    public void iLoginToTheAccount() {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("testTest")
                .build();
        response = restTemplate.postForEntity(baseUrl + "/login", loginRequest, LoginResponse.class);
        assertNotNull(response);
    }

    @Then("The account should be logged in")
    public void iShouldBeLoggedIn() {
        if (response.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test", response.getBody().username());
        testContext.setJwtToken(response.getBody().token().token());
    }

    @When("I logout")
    public void iLogout() {
        String token = testContext.getJwtToken();
        if (token == null) {
            throw new IllegalStateException("JWT token not found. Ensure the login step is executed first.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        response = restTemplate.exchange(
                baseUrl + "/logout",
                HttpMethod.POST,
                request,
                LoginResponse.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Then("The account should be logged out")
    public void iShouldBeLoggedOut() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
