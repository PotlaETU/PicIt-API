package com.picit.post.criteria;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface PostCriteria {

    String USER_ID = "userId";

    static Criteria postsByUserId(String userId) {
        return Criteria.where(USER_ID).is(userId);
    }

    static Criteria postsByHobby(String hobby) {
        return Criteria.where("hobbies").is(hobby);
    }

    static Criteria postsVisibility(List<String> follows) {
        return new Criteria().orOperator(
                Criteria.where(USER_ID).in(follows),
                Criteria.where("isPublic").is(true)
        );
    }

    static Criteria postImageVisibility(List<String> follows, String userId) {
        return new Criteria().orOperator(
                Criteria.where("isPublic").is(true),
                Criteria.where(USER_ID).is(userId),
                Criteria.where(USER_ID).in(follows)
        );
    }
}
