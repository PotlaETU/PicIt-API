package com.picit.stepdefs;

import com.picit.iam.dto.login.LoginRequest;
import com.picit.iam.dto.login.LoginResponse;
import com.picit.iam.dto.login.SignUpRequest;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class IamStepDefs {

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
        response = restTemplate.postForEntity("http://localhost:8081/api/v1/iam/register", signUpRequest, LoginResponse.class);
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
        response = restTemplate.postForEntity("http://localhost:8081/api/v1/iam/login", loginRequest, LoginResponse.class);
        assertNotNull(response);
    }

    @Then("The account should be logged in")
    public void iShouldBeLoggedIn() {
        if (response.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test", response.getBody().username());
    }
}
