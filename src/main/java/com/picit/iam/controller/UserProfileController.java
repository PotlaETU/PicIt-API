package com.picit.iam.controller;

import com.picit.iam.controller.documentation.ProfileControllerDocumentation;
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
    public ResponseEntity<String> addOrUpdateProfilePicture(Authentication authentication, @RequestParam("file") MultipartFile file) {
        return profileService.updateProfilePicture(authentication.getName(), file);
    }

    @GetMapping("/picture")
    public ResponseEntity<byte[]> getProfilePicture(Authentication authentication) {
        return profileService.getProfilePicture(authentication.getName());
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
    public ResponseEntity<String> followUser(Authentication authentication, @RequestParam("username") String username) {
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
    public ResponseEntity<Void> getBlockedUsers(Authentication authentication) {
        return profileService.blockUser(authentication.getName());
    }
}

