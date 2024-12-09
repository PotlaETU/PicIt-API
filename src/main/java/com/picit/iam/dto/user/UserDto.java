package com.picit.iam.dto.user;

import com.picit.iam.entity.Settings;
import com.picit.iam.entity.images.Image;
import lombok.Builder;

import java.util.Arrays;
import java.util.Objects;

@Builder
public record UserDto(
        String id,
        String username,
        String email,
        Image profilePicture,
        String bio,
        String[] hobbies,
        String[] follows,
        Settings settings,
        String createdAt,
        String updatedAt
) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDto userDto = (UserDto) o;
        return Objects.equals(id, userDto.id) && Objects.equals(bio, userDto.bio) && Objects.equals(email, userDto.email) && Objects.equals(username, userDto.username) && Objects.deepEquals(hobbies, userDto.hobbies) && Objects.deepEquals(follows, userDto.follows) && Objects.equals(createdAt, userDto.createdAt) && Objects.equals(updatedAt, userDto.updatedAt) && Objects.equals(settings, userDto.settings) && Objects.equals(profilePicture, userDto.profilePicture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, profilePicture, bio, Arrays.hashCode(hobbies), Arrays.hashCode(follows), settings, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "UserDto {" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", profilePicture=" + profilePicture +
                ", bio='" + bio + '\'' +
                ", hobbies=" + Arrays.toString(hobbies) +
                ", follows=" + Arrays.toString(follows) +
                ", settings=" + settings +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
