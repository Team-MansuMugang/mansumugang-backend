package org.mansumugang.mansumugang_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


/**
 * JpaAuditing 활성화
 * [User 객체에 @CreatedDate : 엔티티 생성시 만들어진 시간 자동 입력] 을 가능하게 해줌.
 */

@Configuration
@EnableJpaAuditing
public class JpaAuditorConfig {
}
