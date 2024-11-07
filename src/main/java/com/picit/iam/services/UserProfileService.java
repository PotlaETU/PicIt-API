package com.picit.iam.services;

import com.picit.iam.mapper.UserMapper;
import com.picit.iam.repository.UserProfileRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserMapper userProfileMapper;

    public ResponseEntity<Void> updateProfilePicture(String userId, String profilePicture) {
        var user = userProfileRepository.findByUserId(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setProfilePicture(profilePicture);
        userProfileRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
