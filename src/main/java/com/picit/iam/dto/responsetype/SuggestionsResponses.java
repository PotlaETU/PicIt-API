package com.picit.iam.dto.responsetype;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.picit.post.entity.Hobby;

import java.util.List;

public record SuggestionsResponses(
        String username,
        @JsonProperty("common_hobbies")
        List<Hobby> commonHobbies,
        String error
) {
}
