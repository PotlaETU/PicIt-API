package com.picit.stepdefs;

import com.picit.post.dto.PostDto;
import com.picit.post.dto.request.PostRequestDto;
import com.picit.post.entity.Hobby;
import com.picit.utils.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class PostsStepDefs {

    @Autowired
    private TestContext testContext;

    private final String baseUrl = "http://localhost:8081/api/v1/post";
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private ResponseEntity<PostDto> responsePostDto;
    private ResponseEntity<PostDto[]> responsePostsDto;


    @When("^I create a post")
    public void iCreateAPost() {
        PostRequestDto testPost = PostRequestDto.builder()
                .hobby(Hobby.DETENTE)
                .content("This is a test post")
                .isPublic(true)
                .build();

        ResponseEntity<PostDto> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                setAuthHeaderWithBody(testPost),
                PostDto.class);
        assertNotNull(response);
        responsePostDto = response;
    }

    @Then("The post should be created")
    public void thePostShouldBeCreated() {
        if (responsePostDto.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }
        assertNotNull(responsePostDto);
        testContext.setPostId(responsePostDto.getBody().id());
        assertEquals(HttpStatus.OK, responsePostDto.getStatusCode());
        assertEquals("This is a test post", responsePostDto.getBody().content());
        assertEquals(Hobby.DETENTE, responsePostDto.getBody().hobby());
    }

    @When("I get the posts by the user")
    public void iGetThePost() {
        ResponseEntity<PostDto[]> response = restTemplate.exchange(
                baseUrl + "/user",
                HttpMethod.GET,
                setAuthHeaderWithBody(null),
                PostDto[].class);
        assertNotNull(response);
        responsePostsDto = response;
    }

    @Then("I should get the posts by the user")
    public void iShouldGetThePost() {
        assertNotNull(responsePostsDto);
        if (responsePostsDto.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }
        assertEquals(HttpStatus.OK, responsePostsDto.getStatusCode());
        assertEquals("This is a test post", responsePostsDto.getBody()[0].content());
        assertEquals(Hobby.DETENTE, responsePostsDto.getBody()[0].hobby());
    }

    @When("I delete a post")
    public void iDeleteThePost() {
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/" + testContext.getPostId(),
                HttpMethod.DELETE,
                setAuthHeaderWithBody(null),
                Void.class);
        assertNotNull(response);
        responsePostDto = null;
    }

    @Then("The post should be deleted")
    public void thePostShouldBeDeleted() {
        ResponseEntity<PostDto[]> response = restTemplate.exchange(
                baseUrl + "/user",
                HttpMethod.GET,
                setAuthHeaderWithBody(null),
                PostDto[].class);
        if (response.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().length);
    }

    private <T> HttpEntity<T> setAuthHeaderWithBody(T body) {
        String token = testContext.getJwtToken();
        if (token == null) {
            throw new IllegalStateException("JWT token not found. Ensure the login step is executed first.");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(body, headers);
    }

}
