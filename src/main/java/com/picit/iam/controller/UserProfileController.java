package com.picit.iam.controller;

import com.picit.iam.controller.documentation.ProfileControllerDocumentation;
import com.picit.iam.dto.user.SuggestedUserDto;
import com.picit.iam.dto.user.UserProfileDto;
import com.picit.iam.services.UserProfileService;
import com.picit.post.entity.Hobby;
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

    @GetMapping("/{username}")
    public UserProfileDto getProfile(Authentication authentication, @PathVariable("username") String username) {
        return profileService.getProfileUsername(authentication.getName(), username);
    }

    @GetMapping("/picture")
    public ResponseEntity<byte[]> getProfilePicture(Authentication authentication) {
        return profileService.getProfilePicture(authentication.getName());
    }

    @GetMapping("/picture/{username}")
    public ResponseEntity<byte[]> getProfilePictureByUsername(Authentication authentication, @PathVariable("username") String username) {
        return profileService.getProfilePictureUser(authentication.getName(), username);
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
    public ResponseEntity<Void> followUser(Authentication authentication, @RequestParam("userId") String userId) {
        return profileService.followUser(authentication.getName(), userId);
    }

    @PostMapping("/unfollow")
    public ResponseEntity<Void> unfollowUser(Authentication authentication, @RequestParam("userId") String userId) {
        return profileService.unfollowUser(authentication.getName(), userId);
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
    public ResponseEntity<Void> blockUser(Authentication authentication, @RequestParam("userId") String userId) {
        return profileService.blockUser(authentication.getName(), userId);
    }

    @GetMapping("/suggestions")
    public List<SuggestedUserDto> getSuggestions(Authentication authentication) {
        return profileService.getSuggestions(authentication.getName());
    }

    @GetMapping("/points")
    public UserProfileDto getPoints(Authentication authentication) {
        return profileService.getPoints(authentication.getName());
    }

    @PostMapping("/hobbies")
    public ResponseEntity<String> addHobbies(Authentication authentication, @RequestBody List<Hobby> hobbies) {
        return profileService.updateHobbies(authentication.getName(), hobbies);
    }
}

