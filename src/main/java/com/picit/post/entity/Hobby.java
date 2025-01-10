package com.picit.post.entity;

import lombok.Getter;

@Getter
public enum Hobby {
    SPORT("SPORT"),
    FILM_SERIE("FILM_SERIE"),
    CUISINE("CUISINE"),
    DETENTE("DETENTE"),
    GAMING("GAMING"),
    LECTURE("LECTURE"),
    MUSIQUE("MUSIQUE"),
    VOYAGE("VOYAGE"),;

    private final String displayName;

    Hobby(String displayName) {
        this.displayName = displayName;
    }

}