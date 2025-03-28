package com.picit.iam.services;

import com.picit.iam.dto.responsetype.SuggestionsResponses;
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
import com.picit.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private static final String USER_ID = "userId";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String USER_NOT_FOUND_POINTS = "Points for user not found";

    @Value("${generate-ai-images.uri}")
    private String urlAi;

    @Value("${generate-ai-images.uri-get-images}")
    private String urlAiGetImages;

    @Value("${suggestion.uri}")
    private String urlSuggestions;

    public ResponseEntity<String> updateProfilePicture(String username, MultipartFile file, Boolean aiGenerated) {
        var userProfile = getUserProfile(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        if (file == null && !Boolean.TRUE.equals(aiGenerated)) {
            return ResponseEntity.badRequest().body("Please select a file to upload or set aiGenerated to true.");
        }
        if (Boolean.TRUE.equals(aiGenerated)) {
            return generateProfilePicAI(userProfile);
        }
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload.");
            }

            ProfilePicImage profilePicture = ProfilePicImage.builder()
                    .userId(userProfile.getUserId())
                    .imageBinary(new Binary(file.getBytes()))
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
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        userProfile.setBio(userProfileDto.bio());
        userProfile.setHobbies(userProfileDto.hobbies());
        userProfile.setFollows(userProfileDto.follows() == null ? new ArrayList<>() : userProfileDto.follows());
        userProfile.setFollowers(userProfileDto.followers() == null ? new ArrayList<>() : userProfileDto.followers());
        userProfile.setBlockedUsers(new ArrayList<>());

        if (pointsRepository.findByUserId(userProfile.getUserId()).isEmpty()) {
            Points points = Points.builder()
                    .userId(userProfile.getUserId())
                    .pointsNb(0)
                    .build();
            pointsRepository.save(points);
        }
        userProfileRepository.save(userProfile);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<String> updateHobbies(String username, List<Hobby> hobbies) {
        var userProfile = getUserProfile(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        Query query = new Query(Criteria.where(USER_ID).is(userProfile.getUserId()));
        Update update = new Update().set("hobbies", hobbies);
        mongoTemplate.updateFirst(query, update, UserProfile.class);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<byte[]> getProfilePicture(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        var userProfile = userProfileRepository.findByUserId(user.getId());

        Image profilePic = userProfile.getProfilePicture();
        if (profilePic != null) {
            byte[] profilePicData = userProfile
                    .getProfilePicture()
                    .getImageBinary()
                    .getData();
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(profilePicData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<byte[]> getProfilePictureUser(String username, String userIdToGet) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        User userToGet = userRepository.findById(userIdToGet)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        var userProfileToGet = userProfileRepository.findByUserId(userToGet.getId());

        if (userProfileToGet.getBlockedUsers().contains(user.getId())) {
            return ResponseEntity.notFound().build();
        }

        Image profilePic = userProfileToGet.getProfilePicture();
        if (profilePic != null) {
            byte[] profilePicData = userProfileToGet
                    .getProfilePicture()
                    .getImageBinary()
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
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND))
                .getId();
        var userProfile = userProfileRepository.findByUserId(userId);
        if (userProfile == null) {
            return Optional.empty();
        }
        return Optional.of(userProfile);
    }

    public List<UserProfileDto> searchProfiles(String username, String query) {
        var profile = userProfileRepository.findByUsernameRegex(".*" + query + ".*")
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        return profile.stream()
                .filter(u -> !u.getUsername().equals(username))
                .filter(u -> !u.getBlockedUsers()
                        .contains(user.getId()))
                .map(u -> {
                    var points = pointsRepository.findByUserId(u.getUserId())
                            .orElse(Points.builder()
                                    .pointsNb(0)
                                    .build());
                    Long postCount = postRepository.countPostByUserId(u.getUserId());
                    return userProfileMapper.toUserProfileDto(u, points, postCount);
                })
                .toList();
    }

    public ResponseEntity<Void> blockUser(String name, String userIdToBlock) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        var userToBlock = userRepository.findById(userIdToBlock)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        Query query = new Query(Criteria.where(USER_ID).is(userProfile.getUserId()));
        Update update;
        if (userProfile.getBlockedUsers().contains(userToBlock.getId())) {
            update = new Update().pull("blockedUsers", userToBlock.getId());
        } else {
            update = new Update().addToSet("blockedUsers", userToBlock.getId());
        }
        mongoTemplate.updateFirst(query, update, UserProfile.class);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Void> followUser(String name, String userIdToFollow) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        var userToFollow = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        Query followerQuery = new Query(Criteria.where(USER_ID).is(userProfile.getUserId()));
        Update followerUpdate = new Update().addToSet("follows", userIdToFollow);
        mongoTemplate.updateFirst(followerQuery, followerUpdate, UserProfile.class);

        Query followedQuery = new Query(Criteria.where(USER_ID).is(userToFollow.getId()));
        Update followedUpdate = new Update().addToSet("followers", userProfile.getUserId());
        mongoTemplate.updateFirst(followedQuery, followedUpdate, UserProfile.class);

        return ResponseEntity.ok().build();
    }

    public List<UserProfileDto> getFollowing(String name) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        List<UserProfile> userFollows = new ArrayList<>();
        userProfile.getFollows()
                .forEach(s -> userFollows.add(userProfileRepository.findByUserId(s)));

        return userFollows.stream()
                .map(userProfileMapper::toUserProfileDto)
                .toList();
    }

    public List<UserProfileDto> getFollowers(String name) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        List<UserProfile> userFollows = new ArrayList<>();
        userProfile.getFollowers()
                .forEach(s -> userFollows.add(userProfileRepository.findByUserId(s)));

        return userFollows.stream()
                .map(userProfileMapper::toUserProfileDto)
                .toList();
    }

    public List<UserProfileDto> getFollowers(String name, String username) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        var userProfileToGet = getUserProfile(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        if (userProfileToGet.getBlockedUsers().contains(userProfile.getUserId())) {
            throw new UserNotFound(USER_NOT_FOUND);
        }
        List<UserProfile> userFollows = new ArrayList<>();
        userProfileToGet.getFollowers()
                .forEach(s -> userFollows.add(userProfileRepository.findByUserId(s)));


        return userFollows.stream()
                .map(userProfileMapper::toUserProfileDto)
                .toList();
    }

    public ResponseEntity<Void> unfollowUser(String name, String userId) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        var userToUnfollow = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        Query followerQuery = new Query(Criteria.where(USER_ID).is(userProfile.getUserId()));
        Update followerUpdate = new Update().pull("follows", userToUnfollow.getId());
        mongoTemplate.updateFirst(followerQuery, followerUpdate, UserProfile.class);

        Query followedQuery = new Query(Criteria.where(USER_ID).is(userToUnfollow.getId()));
        Update followedUpdate = new Update().pull("followers", userProfile.getUserId());
        mongoTemplate.updateFirst(followedQuery, followedUpdate, UserProfile.class);

        return ResponseEntity.ok().build();
    }

    public List<SuggestedUserDto> getSuggestions(String name) {
        var userId = userRepository.findByUsername(name)
                .map(User::getId)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        ResponseEntity<SuggestionsResponses[]> response = restTemplate.getForEntity(urlSuggestions + userId, SuggestionsResponses[].class);
        SuggestionsResponses[] jsonResponse = response.getBody();
        if (jsonResponse == null) {
            throw new IllegalArgumentException("Error getting suggestions");
        }
        if (jsonResponse[0].error() != null) {
            throw new UserNotFound(USER_NOT_FOUND);
        }
        List<SuggestedUserDto> suggestedUsers = new ArrayList<>();
        for (SuggestionsResponses suggestionsResponses : jsonResponse) {
            var userProfile = userProfileRepository.findByUsernameRegex(suggestionsResponses.username())
                    .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
            SuggestedUserDto suggestedUser = SuggestedUserDto.builder()
                    .userProfile(userProfileMapper.toUserProfileDto(userProfile.getFirst()))
                    .commonHobbies(suggestionsResponses.commonHobbies())
                    .build();
            suggestedUsers.add(suggestedUser);
        }
        return suggestedUsers;
    }

    public UserProfileDto getPoints(String name, String userIdToGet) {
        if (userIdToGet == null){
            var userId = getUser(name).getId();
            var points = pointsRepository.findByUserId(userId)
                    .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND_POINTS));
            return UserProfileDto.builder()
                    .points(points.getPointsNb())
                    .build();
        } else {
            var user = userRepository.findById(userIdToGet
            ).orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
            var points = pointsRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND_POINTS));
            return UserProfileDto.builder()
                    .username(user.getUsername())
                    .points(points.getPointsNb())
                    .build();
        }
    }

    public UserProfileDto getProfile(String username) {
        var profile = getUserProfile(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        var points = pointsRepository.findByUserId(profile.getUserId())
                .orElse(Points.builder()
                        .pointsNb(0)
                        .build());
        Long postCount = postRepository.countPostByUserId(profile.getUserId());
        return userProfileMapper.toUserProfileDto(profile, points, postCount);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
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
                .imageBinary(new Binary(image))
                .build();
        profilePicRepository.save(imageGenerated);
        return ResponseEntity.ok().build();
    }

    public UserProfileDto getProfileUsername(String username, String usernameToGet) {
        var profile = userProfileRepository.findByUsername(usernameToGet)
                .orElse(userRepository.findByUsername(usernameToGet)
                        .map(u -> userProfileRepository.findByUserId(u.getId()))
                        .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND)));

        var userToGet = userRepository.findByUsername(usernameToGet)
                .orElse(userRepository.findByUsername(usernameToGet)
                        .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND)));

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));

        if (profile == null || profile.getBlockedUsers().contains(user.getId())) {
            throw new UserNotFound(USER_NOT_FOUND);
        }
        if (profile.getUserId().equals(user.getId())) {
            return getProfile(username);
        }
        if (!profile.getFollowers().contains(user.getId()) && "private".equals(userToGet.getSettings().getPrivacy())) {
            return UserProfileDto.builder()
                    .bio(profile.getBio())
                    .username(profile.getUsername())
                    .postCount(postRepository.countPostByUserId(profile.getUserId()))
                    .followers(profile.getFollowers())
                    .follows(profile.getFollows())
                    .userId(profile.getUserId())
                    .build();
        }

        var points = pointsRepository.findByUserId(profile.getUserId())
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND_POINTS));
        Long postCount = postRepository.countPostByUserId(profile.getUserId());
        return userProfileMapper.toUserProfileDto(profile, points, postCount);
    }

    public ResponseEntity<String> updateBio(String name, UserProfileDto bio) {
        var userProfile = getUserProfile(name)
                .orElseThrow(() -> new UserNotFound(USER_NOT_FOUND));
        Query query = new Query(Criteria.where(USER_ID).is(userProfile.getUserId()));
        Update update = new Update().set("bio", bio.bio());
        mongoTemplate.updateFirst(query, update, UserProfile.class);
        return ResponseEntity.ok().build();
    }
}
