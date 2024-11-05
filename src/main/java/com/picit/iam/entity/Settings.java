package com.picit.iam.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Settings {
    private String privacy;
    private boolean notifications;
}