package com.picit.stepdefs;

import com.picit.iam.dto.login.LoginResponse;
import com.picit.iam.dto.login.SignUpRequest;
import com.picit.iam.dto.user.UserProfileDto;
import com.picit.post.entity.Hobby;
import com.picit.utils.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProfileStepdefs {

    private final String baseUrl = "http://localhost:8081/api/v1/profile";
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private ResponseEntity<UserProfileDto> responseProfileDto;
    private UserProfileDto responseProfileDtoSearch;

    @When("I get my profile")
    public void iGetMyProfile() {
        responseProfileDto = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                TestContext.setAuthHeaderWithBody(null),
                UserProfileDto.class);
        assertNotNull(responseProfileDto);
    }

    @Then("I should see my profile")
    public void theProfileShouldBeReturned() {
        if (responseProfileDto.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }
        assertNotNull(responseProfileDto);
        assertEquals("test", responseProfileDto.getBody().username());
        assertEquals(1, responseProfileDto.getBody().postCount());
    }

    @When("I get the profile of {string}")
    public void iGetTheProfileOf(String username) {
        responseProfileDto = restTemplate.exchange(
                baseUrl + "/" + username,
                HttpMethod.GET,
                TestContext.setAuthHeaderWithBody(null),
                UserProfileDto.class);
        assertNotNull(responseProfileDto);
    }

    @Then("I should see the profile of {string}")
    public void iShouldSeeTheProfileOf(String username) {
        if (responseProfileDto.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }
        assertNotNull(responseProfileDto);
        assertEquals(username, responseProfileDto.getBody().username());
        assertEquals(0, responseProfileDto.getBody().postCount());
    }

    @When("I create my profile")
    public void iCreateAProfile() {
        UserProfileDto userProfileDto = UserProfileDto.builder()
                .bio("test")
                .username("test")
                .hobbies(List.of(Hobby.CUISINE, Hobby.DETENTE, Hobby.MUSIQUE))
                .build();
        responseProfileDto = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                TestContext.setAuthHeaderWithBody(userProfileDto),
                UserProfileDto.class);
        assertNotNull(responseProfileDto);
        assertEquals(HttpStatus.OK, responseProfileDto.getStatusCode());
    }

    @Then("I should see my created profile")
    public void iShouldSeeTheCreatedProfile() {
        responseProfileDto = restTemplate.exchange(
                baseUrl,
                HttpMethod.GET,
                TestContext.setAuthHeaderWithBody(null),
                UserProfileDto.class);
        assertNotNull(responseProfileDto);
        assertNotNull(responseProfileDto.getBody());
        assertEquals("test", responseProfileDto.getBody().username());
        assertEquals("test", responseProfileDto.getBody().bio());
        assertEquals(1, responseProfileDto.getBody().postCount());
    }

    @When("I create a new user with username {string} and email {string}")
    public void iCreateANewUser(String username, String email) {
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username(username)
                .email(email)
                .password("password")
                .notifications(true)
                .privacy(true)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignUpRequest> requestEntity = new HttpEntity<>(signUpRequest, headers);

        ResponseEntity<LoginResponse> response = restTemplate.exchange(
                "http://localhost:8081/api/v1/iam/register",
                HttpMethod.POST,
                requestEntity,
                LoginResponse.class
        );

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @When("I search for the profile {string}")
    public void iSearchForTheProfile(String query) {
        ResponseEntity<UserProfileDto[]> responseProfilesDto = restTemplate.exchange(
                baseUrl + "/search?query=" + query,
                HttpMethod.GET,
                TestContext.setAuthHeaderWithBody(null),
                UserProfileDto[].class);

        assertNotNull(responseProfilesDto);
        assertNotNull(responseProfilesDto.getBody());
        responseProfileDtoSearch = responseProfilesDto.getBody()[0];
    }

    @Then("I should see the profile {string}")
    public void iShouldSeeTheProfile(String username) {
        assertNotNull(responseProfileDtoSearch);
        assertEquals(username, responseProfileDtoSearch.username());
    }
}
