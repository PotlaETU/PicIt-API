package com.picit.iam.services;

import com.picit.iam.dto.user.UserProfileDto;
import com.picit.iam.entity.User;
import com.picit.iam.entity.UserProfile;
import com.picit.iam.entity.images.Image;
import com.picit.iam.entity.images.ProfilePicImage;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.mapper.UserMapper;
import com.picit.iam.repository.ProfilePicRepository;
import com.picit.iam.repository.UserProfileRepository;
import com.picit.iam.repository.UserRepository;
import com.picit.post.entity.Hobby;
import lombok.AllArgsConstructor;
import org.bson.types.Binary;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserMapper userProfileMapper;
    private final UserRepository userRepository;
    private final ProfilePicRepository profilePicRepository;


    public ResponseEntity<String> updateProfilePicture(String username, MultipartFile file) {
        var userProfile = getUserProfile(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload.");
            }

            ProfilePicImage profilePicture = ProfilePicImage.builder()
                    .userId(userProfile.getUserId())
                    .image(new Binary(file.getBytes()))
                    .aiGenerated(false)
                    .build();

            profilePicRepository.save(profilePicture);
            userProfile.setProfilePicture(profilePicture);
            userProfileRepository.save(userProfile);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error saving image" + e.getMessage());
        }
    }

    public ResponseEntity<String> createProfile(String username, UserProfileDto userProfileDto) {
        var userProfile = getUserProfile(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
        userProfile.setBio(userProfileDto.bio());
        userProfile.setHobbies(userProfileDto.hobbies());
        userProfile.setFollows(userProfileDto.follows());

        userProfileRepository.save(userProfile);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<String> updateHobbies(String username, List<Hobby> hobbies) {
        var userProfile = getUserProfile(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
        userProfile.setHobbies(hobbies);
        userProfileRepository.save(userProfile);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<byte[]> getProfilePicture(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
        var userProfile = userProfileRepository.findByUserId(user.getId());

        Image profilePic = userProfile.getProfilePicture();
        if (profilePic != null) {
            byte[] profilePicData = userProfile
                    .getProfilePicture()
                    .getImage()
                    .getData();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(profilePicData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Optional<UserProfile> getUserProfile(String username) {
        var userId = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"))
                .getId();
        var userProfile = userProfileRepository.findByUserId(userId);
        if (userProfile == null) {
            return Optional.empty();
        }
        return Optional.of(userProfile);
    }

    public List<UserProfileDto> searchProfiles(String query) {
        var profile = userProfileRepository.findByUsernameRegex(".*" + query + ".*")
                .orElseThrow(() -> new UserNotFound("User not found"));

        return profile.stream()
                .map(userProfileMapper::toUserProfileDto)
                .toList();
    }

    public ResponseEntity<Void> blockUser(String name, String usernameUserToBlock) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound("User not found"));
        var userToBlock = userRepository.findByUsername(usernameUserToBlock)
                .orElseThrow(() -> new UserNotFound("User not found"));
        userProfile.getBlockedUsers().add(userToBlock.getId());
        userProfileRepository.save(userProfile);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> followUser(String name, String usernameUserToFollow) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound("User not found"));
        var userToFollow = userRepository.findByUsername(usernameUserToFollow)
                .orElseThrow(() -> new UserNotFound("User not found"));
        userProfile.getFollows().add(userToFollow.getId());
        var userToFollowProfile = userProfileRepository.findByUserId(userToFollow.getId());
        userToFollowProfile.getFollowers().add(userProfile.getUserId());
        userProfileRepository.save(userToFollowProfile);
        userProfileRepository.save(userProfile);
        return ResponseEntity.ok().build();
    }

    public List<UserProfileDto> getFollowing(String name) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound("User not found"));
        List<UserProfile> userFollows = new ArrayList<>();
        userProfile.getFollows()
                .forEach(s -> userFollows.add(userProfileRepository.findByUserId(s)));

        return userFollows.stream()
                .map(userProfileMapper::toUserProfileDto)
                .toList();
    }

    public List<UserProfileDto> getFollowers(String name) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound("User not found"));
        List<UserProfile> userFollows = new ArrayList<>();
        userProfile.getFollowers()
                .forEach(s -> userFollows.add(userProfileRepository.findByUserId(s)));

        return userFollows.stream()
                .map(userProfileMapper::toUserProfileDto)
                .toList();
    }

    public ResponseEntity<String> unfollowUser(String name, String username) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound("User not found"));
        var userToUnfollow = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
        userProfile.getFollows().remove(userToUnfollow.getId());
        var userToUnfollowProfile = userProfileRepository.findByUserId(userToUnfollow.getId());
        userToUnfollowProfile.getFollowers().remove(userProfile.getUserId());
        userProfileRepository.save(userToUnfollowProfile);
        userProfileRepository.save(userProfile);
        return ResponseEntity.ok().build();
    }
}
