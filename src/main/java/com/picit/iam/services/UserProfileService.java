package com.picit.iam.services;

import com.picit.iam.dto.responseType.SuggestionsResponses;
import com.picit.iam.dto.user.SuggestedUserDto;
import com.picit.iam.dto.user.UserProfileDto;
import com.picit.iam.entity.User;
import com.picit.iam.entity.UserProfile;
import com.picit.iam.entity.images.Image;
import com.picit.iam.entity.images.ProfilePicImage;
import com.picit.iam.entity.points.Points;
import com.picit.iam.exceptions.UserNotFound;
import com.picit.iam.mapper.UserMapper;
import com.picit.iam.repository.UserProfileRepository;
import com.picit.iam.repository.UserRepository;
import com.picit.iam.repository.points.PointsRepository;
import com.picit.iam.repository.profilepic.ProfilePicRepository;
import com.picit.post.entity.Hobby;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserMapper userProfileMapper;
    private final UserRepository userRepository;
    private final ProfilePicRepository profilePicRepository;
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();
    private final PointsRepository pointsRepository;

    @Value("${generate-ai-images.uri}")
    private String urlAi;

    @Value("${generate-ai-images.uri-get-images}")
    private String urlAiGetImages;

    @Value("${suggestion.uri}")
    private String urlSuggestions;

    public ResponseEntity<String> updateProfilePicture(String username, MultipartFile file, Boolean aiGenerated) {
        var userProfile = getUserProfile(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
        if (file == null && !aiGenerated) {
            return ResponseEntity.badRequest().body("Please select a file to upload or set aiGenerated to true.");
        }
        if (aiGenerated) {
            return generateProfilePicAI(userProfile);
        }
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

        Points points = Points.builder()
                .userId(userProfile.getUserId())
                .points(0)
                .build();

        pointsRepository.save(points);
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

    public List<SuggestedUserDto> getSuggestions(String name) {
        var userId = userRepository.findByUsername(name)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound("User not found"));
        ResponseEntity<SuggestionsResponses[]> response = restTemplate.getForEntity(urlSuggestions + userId, SuggestionsResponses[].class);
        SuggestionsResponses[] jsonResponse = response.getBody();
        if (jsonResponse == null) {
            throw new IllegalArgumentException("Error getting suggestions");
        }
        if (jsonResponse[0].error() != null) {
            throw new UserNotFound("User not found");
        }
        List<SuggestedUserDto> suggestedUsers = new ArrayList<>();
        for (SuggestionsResponses suggestionsResponses : jsonResponse) {
            var userProfile = userProfileRepository.findByUsernameRegex(suggestionsResponses.username())
                    .orElseThrow(() -> new UserNotFound("User not found"));
            SuggestedUserDto suggestedUser = SuggestedUserDto.builder()
                    .userProfile(userProfileMapper.toUserProfileDto(userProfile.getFirst()))
                    .commonHobbies(suggestionsResponses.commonHobbies())
                    .build();
            suggestedUsers.add(suggestedUser);
        }
        return suggestedUsers;
    }

    public UserProfileDto getPoints(String name) {
        var userId = getUser(name).getId();
        var points = pointsRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFound("Points for user not found"));
        return UserProfileDto.builder()
                .points(points.getPoints())
                .build();
    }

    public UserProfileDto getProfile(String username) {
        var profile = getUserProfile(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
        var points = pointsRepository.findByUserId(profile.getUserId())
                .orElseThrow(() -> new UserNotFound("Points for user not found"));
        return userProfileMapper.toUserProfileDto(profile, points);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
    }

    private ResponseEntity<String> generateProfilePicAI(UserProfile userProfile) {
        urlAi = urlAi + "generate_profile_pic";
        var res = restTemplate.postForEntity(urlAi, null, String.class);
        if (res.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.internalServerError()
                    .build();
        }
        var image = restTemplate.getForEntity(urlAiGetImages + "image_1.png", byte[].class).getBody();
        if (image == null) {
            return ResponseEntity.internalServerError().body("Error getting image");
        }
        var imageGenerated = ProfilePicImage.builder()
                .userId(userProfile.getUserId())
                .aiGenerated(true)
                .image(new Binary(image))
                .build();
        profilePicRepository.save(imageGenerated);
        return ResponseEntity.ok().build();
    }

}
