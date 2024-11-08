package com.picit.iam.controller.documentation;

import com.picit.iam.dto.user.UserProfileDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Profile", description = "Profile management")
public interface ProfileControllerDocumentation {


    ResponseEntity<String> addOrUpdateProfilePicture(Authentication authentication, @RequestParam("file") MultipartFile file);

    ResponseEntity<byte[]> getProfilePicture(Authentication authentication);

    ResponseEntity<String> createProfile(Authentication authentication, @RequestBody UserProfileDto bio);
}
