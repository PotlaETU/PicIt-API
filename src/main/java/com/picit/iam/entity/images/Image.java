package com.picit.iam.entity.images;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class Image {
    @Id
    private String id;
    private Binary imageBinary;
    private Boolean aiGenerated;
}
