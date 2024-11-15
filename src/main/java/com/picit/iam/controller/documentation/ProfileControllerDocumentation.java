package com.picit.iam.controller.documentation;

import com.picit.iam.dto.user.UserProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Profile", description = "Profile management")
public interface ProfileControllerDocumentation {

    @Operation(summary = "Add or update profile picture", description = "Uploads a new profile picture or updates the existing one")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile picture updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    ResponseEntity<String> addOrUpdateProfilePicture(Authentication authentication, MultipartFile file, boolean aiGenerated);

    @Operation(summary = "Get profile picture", description = "Retrieves the profile picture of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile picture retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Profile picture not found")
    })
    ResponseEntity<byte[]> getProfilePicture(Authentication authentication);

    @Operation(summary = "Create or update profile", description = "Creates a new profile or updates the existing profile of the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile created or updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    ResponseEntity<String> createProfile(Authentication authentication, @RequestBody(
            description = "The user profile data",
            required = true) UserProfileDto bio);
}