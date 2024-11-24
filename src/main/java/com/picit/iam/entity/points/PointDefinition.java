package com.picit.iam.entity.points;

import lombok.Getter;

@Getter
public enum PointDefinition {
    CREATE_POST(10),
    LIKE_POST(1),
    DISLIKE_POST(-1);

    private final int points;

    PointDefinition(int points) {
        this.points = points;
    }
}
