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
import lombok.AllArgsConstructor;
import org.bson.types.Binary;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
                    .userId(file.getOriginalFilename())
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

    public ResponseEntity<String> updateHobbies(String username, String[] hobbies) {
        var userProfile = getUserProfile(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
        userProfile.setHobbies(hobbies);
        userProfileRepository.save(userProfile);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<byte[]> getProfilePicture(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));

        Image profilePic = user.getUserProfile().getProfilePicture();
        if (profilePic != null) {
            byte[] profilePicData = user.getUserProfile()
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

}
