package com.picit.iam.controller.documentation;

import com.picit.iam.dto.login.LoginRequest;
import com.picit.iam.dto.login.LoginResponse;
import com.picit.iam.dto.login.SignUpRequest;
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
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    ResponseEntity<LoginResponse> signUp(@RequestBody(
            description = "The user to sign up",
            required = true) SignUpRequest signUpRequest);

    ResponseEntity<LoginResponse> login(LoginRequest loginRequest);

    ResponseEntity<TokenResponse> refresh(TokenRefreshRequest tokenRefreshRequest, Authentication authentication, HttpServletRequest request);

    UserDto getMe(Authentication authentication);

    ResponseEntity<Void> logout(HttpServletRequest request);
}
