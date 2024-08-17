package org.mansumugang.mansumugang_service.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileChecker {
    private final Environment environment;

    // 특정 프로파일이 활성화 되어있는지 확인
    public boolean checkActiveProfile(String profile) {
        for (String profileName : environment.getActiveProfiles()) {
            if (profileName.equals(profile)) {
                return true;
            }
        }
        return false;
    }
}
