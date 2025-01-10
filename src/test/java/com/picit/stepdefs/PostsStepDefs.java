package com.picit.stepdefs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picit.post.dto.PostDto;
import com.picit.post.dto.request.PostRequestDto;
import com.picit.post.entity.Hobby;
import com.picit.utils.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;

import static com.picit.utils.TestContext.setAuthHeaderWithBody;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class PostsStepDefs {
    private final String baseUrl = "http://localhost:8081/api/v1/post";
    private final TestRestTemplate restTemplate = new TestRestTemplate();
    private ResponseEntity<PostDto> responsePostDto;
    private ResponseEntity<PostDto[]> responsePostsDto;


    @When("I create a post")
    public void iCreateAPost() {
        PostRequestDto testPost = PostRequestDto.builder()
                .hobby(Hobby.DETENTE)
                .content("This is a test post")
                .isPublic(true)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String postDataJson;
        try {
            postDataJson = objectMapper.writeValueAsString(testPost);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize PostRequestDto", e);
        }

        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("postData", new HttpEntity<>(postDataJson, createJsonHeaders()));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers = TestContext.setAuthHeaderWithBody(headers).getHeaders();
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<PostDto> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                requestEntity,
                PostDto.class
        );

        assertNotNull(response);
        assertNotNull(response.getBody());
        responsePostDto = response;
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Then("The post should be created")
    public void thePostShouldBeCreated() {
        if (responsePostDto.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }
        assertNotNull(responsePostDto);
        TestContext.setPostId(responsePostDto.getBody().id());
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
                baseUrl + "/" + TestContext.getPostId(),
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
        assertEquals(1, response.getBody().length);
    }

    @When("I update a post")
    public void iUpdateThePost() {
        PostRequestDto testPost = PostRequestDto.builder()
                .hobby(Hobby.DETENTE)
                .content("This is a test post")
                .isPublic(true)
                .build();

        ResponseEntity<PostDto> response = restTemplate.exchange(
                baseUrl + "/" + TestContext.getPostId(),
                HttpMethod.PUT,
                setAuthHeaderWithBody(testPost),
                PostDto.class);
        assertNotNull(response);
        responsePostDto = response;
    }

    @Then("The post should be updated")
    public void thePostShouldBeUpdated() {
        if (responsePostDto.getBody() == null) {
            throw new RuntimeException("Response body is null");
        }
        assertNotNull(responsePostDto);
        assertEquals(HttpStatus.OK, responsePostDto.getStatusCode());
        assertEquals("This is a test post", responsePostDto.getBody().content());
        assertEquals(Hobby.DETENTE, responsePostDto.getBody().hobby());
    }


    @When("I create a post using json")
    public void iCreateAPostUsingJson() {
        PostRequestDto testPost = PostRequestDto.builder()
                .hobby(Hobby.DETENTE)
                .content("This is a test post")
                .isPublic(true)
                .build();

        ResponseEntity<PostDto> response = restTemplate.exchange(
                baseUrl + "/json",
                HttpMethod.POST,
                setAuthHeaderWithBody(testPost),
                PostDto.class
        );

        assertNotNull(response);
        assertNotNull(response.getBody());
        responsePostDto = response;
    }

    @When("I like a post")
    public void iLikeAPost() {
        String baseUrlLikes = "http://localhost:8081/api/v1/like";
        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrlLikes + "?postId=" + TestContext.getPostId(),
                HttpMethod.POST,
                setAuthHeaderWithBody(null),
                Void.class);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Then("The post should be liked")
    public void thePostShouldBeLiked() {
        ResponseEntity<PostDto> response = restTemplate.exchange(
                baseUrl + "/" + TestContext.getPostId(),
                HttpMethod.GET,
                setAuthHeaderWithBody(null),
                PostDto.class);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().likes().toArray().length);
    }
}
