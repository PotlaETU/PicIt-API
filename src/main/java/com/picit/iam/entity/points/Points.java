package com.picit.iam.entity.points;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "points")
public class Points {
    @Id
    private String id;

    private String userId;

    private int points;
}
