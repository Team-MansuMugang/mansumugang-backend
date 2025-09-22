package org.mansumugang.mansumugang_service.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AudioFileSaveDto {
    private String fileName;
    private Long audioDuration;
}
