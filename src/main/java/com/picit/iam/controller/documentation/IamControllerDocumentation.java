package com.picit.iam.controller.documentation;

import com.picit.iam.dto.login.LoginRequest;
import com.picit.iam.dto.login.LoginResponse;
import com.picit.iam.dto.login.SignUpRequest;
import com.picit.iam.dto.responsetype.MessageResponse;
import com.picit.iam.dto.token.TokenRefreshRequest;
import com.picit.iam.dto.token.TokenResponse;
import com.picit.iam.dto.user.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@Tag(name = "IAM", description = "IAM for user authentication")
public interface IamControllerDocumentation {

    @Operation(summary = "Sign up a new user", description = "Returns a JWT and refresh token assigned to the user. The JWT token is also set as a cookie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    ResponseEntity<LoginResponse> signUp(@RequestBody(
            description = "The user to sign up",
            required = true) SignUpRequest signUpRequest);

    @Operation(summary = "Log in a user", description = "Returns a JWT and refresh token assigned to the user. The JWT token is also set as a cookie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<LoginResponse> login(@RequestBody(
            description = "The user login request",
            required = true) LoginRequest loginRequest);

    @Operation(summary = "Refresh the JWT token", description = "Returns a new JWT and refresh token assigned to the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
    })
    ResponseEntity<TokenResponse> refresh(@RequestBody(
            description = "The token refresh request",
            required = true) TokenRefreshRequest tokenRefreshRequest, HttpServletRequest request);

    @Operation(summary = "Get the current user", description = "Returns the details of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    UserDto getMe(Authentication authentication);

    @Operation(summary = "Log out the current user", description = "Logs out the current user and clears the JWT and refresh token cookies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged out successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    ResponseEntity<MessageResponse> logout(HttpServletRequest request);

    @Operation(summary = "Reset the user's password", description = "Resets the user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<MessageResponse> resetPassword(String oldPassword, String newPassword, Authentication authentication);
}