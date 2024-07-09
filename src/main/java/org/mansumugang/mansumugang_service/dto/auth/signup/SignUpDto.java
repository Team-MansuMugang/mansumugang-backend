package org.mansumugang.mansumugang_service.dto.auth.signup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.mansumugang.mansumugang_service.domain.user.User;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class SignUpDto {

    private String username;
    private String authority;
    private String usertype;

    public static SignUpDto fromEntity(User user){
        return SignUpDto.builder()
                .username(user.getUsername())
                .usertype(user.getUsertype())
                .authority(user.getAuthority())
                .build();
    }
}
