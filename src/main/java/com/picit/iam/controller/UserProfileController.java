package com.picit.iam.controller;

import com.picit.iam.controller.documentation.ProfileControllerDocumentation;
import com.picit.iam.dto.user.SuggestedUserDto;
import com.picit.iam.dto.user.UserProfileDto;
import com.picit.iam.services.UserProfileService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/profile")
public class UserProfileController implements ProfileControllerDocumentation {

    private final UserProfileService profileService;

    @PostMapping("/picture")
    public ResponseEntity<String> addOrUpdateProfilePicture(Authentication authentication,
                                                            @RequestParam(value = "file", required = false) MultipartFile file,
                                                            @RequestParam(value = "aiGenerated", required = false, defaultValue = "false") Boolean aiGenerated) {
        return profileService.updateProfilePicture(authentication.getName(), file, aiGenerated);
    }

    @GetMapping
    public UserProfileDto getProfile(Authentication authentication) {
        return profileService.getProfile(authentication.getName());
    }

    @GetMapping("/{userId}")
    public UserProfileDto getProfile(Authentication authentication, @PathVariable("userId") String userId) {
        return profileService.getProfileUserId(authentication.getName(), userId);
    }

    @GetMapping("/picture")
    public ResponseEntity<byte[]> getProfilePicture(Authentication authentication) {
        return profileService.getProfilePicture(authentication.getName());
    }

    @GetMapping("/picture/{userId}")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable("userId") String userId) {
        return profileService.getProfilePicture(userId);
    }

    @PostMapping
    public ResponseEntity<String> createProfile(Authentication authentication, @RequestBody UserProfileDto bio) {
        return profileService.createProfile(authentication.getName(), bio);
    }

    @GetMapping("/search")
    public List<UserProfileDto> searchProfiles(@RequestParam("query") String query) {
        return profileService.searchProfiles(query);
    }

    @PostMapping("/follow")
    public ResponseEntity<Void> followUser(Authentication authentication, @RequestParam("username") String username) {
        return profileService.followUser(authentication.getName(), username);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(Authentication authentication, @RequestParam("username") String username) {
        return profileService.unfollowUser(authentication.getName(), username);
    }

    @GetMapping("/followers")
    public List<UserProfileDto> getFollowers(Authentication authentication) {
        return profileService.getFollowers(authentication.getName());
    }

    @GetMapping("/following")
    public List<UserProfileDto> getFollowing(Authentication authentication) {
        return profileService.getFollowing(authentication.getName());
    }

    @PostMapping("/block")
    public ResponseEntity<Void> blockUser(Authentication authentication, @RequestParam("username") String username) {
        return profileService.blockUser(authentication.getName(), username);
    }

    @GetMapping("/suggestions")
    public List<SuggestedUserDto> getSuggestions(Authentication authentication) {
        return profileService.getSuggestions(authentication.getName());
    }

    @GetMapping("/points")
    public UserProfileDto getPoints(Authentication authentication) {
        return profileService.getPoints(authentication.getName());
    }
}

