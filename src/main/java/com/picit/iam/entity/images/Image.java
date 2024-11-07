package com.picit.iam.entity.images;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@SuperBuilder
public abstract class Image {
    @Id
    private String id;
    private Binary image;
    private Boolean aiGenerated;
}
