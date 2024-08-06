package org.mansumugang.mansumugang_service.dto.auth.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ReissueTokenDto {
    private String token;

    public static ReissueTokenDto of(String token){
        return ReissueTokenDto.builder()
                .token(token)
                .build();
    }

}
