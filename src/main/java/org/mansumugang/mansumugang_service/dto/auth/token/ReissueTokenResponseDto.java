package org.mansumugang.mansumugang_service.dto.auth.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReissueTokenResponseDto {
    private String accessToken;

    public static ReissueTokenResponseDto fromDto(ReissueTokenDto reissueTokenDto){
        return ReissueTokenResponseDto.builder()
                .accessToken(reissueTokenDto.getToken())
                .build();
    }
}
