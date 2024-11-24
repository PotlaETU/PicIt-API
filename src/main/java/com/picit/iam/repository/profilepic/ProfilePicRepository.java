package com.picit.iam.repository.profilepic;

import com.picit.iam.entity.images.Image;
import com.picit.iam.entity.images.ProfilePicImage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfilePicRepository extends MongoRepository<ProfilePicImage, String> {
    Image findByUserId(String userId);
}
