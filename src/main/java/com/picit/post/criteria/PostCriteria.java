package com.picit.post.criteria;

import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface PostCriteria {

    static Criteria postsByUserId(String userId) {
        return Criteria.where("userId").is(userId);
    }

    static Criteria postsByHobby(String hobby) {
        return Criteria.where("hobbies").is(hobby);
    }

    static Criteria postsVisibility(List<String> follows) {
        return Criteria.where("userId").in(follows)
                .orOperator(Criteria.where("isPublic").is(true));
    }
}
