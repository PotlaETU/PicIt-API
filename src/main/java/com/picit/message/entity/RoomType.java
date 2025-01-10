package com.picit.message.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RoomType {
    PRIVATE("private"),
    GROUP("group");

    private final String name;

    RoomType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getValue() {
        return this.name;
    }

    @JsonCreator
    public static RoomType fromValue(String value) {
        for (RoomType type : RoomType.values()) {
            if (type.name.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum type " + value);
    }

}
