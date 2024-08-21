package org.mansumugang.mansumugang_service.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileType {
    AUDIO("audios/"),
    IMAGE("images/"),
    POST_IMAGE("postImages/");

    private final String s3Path;
}
