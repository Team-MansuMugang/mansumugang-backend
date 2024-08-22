package org.mansumugang.mansumugang_service.domain.user;

import jakarta.persistence.*;
import lombok.*;

import org.mansumugang.mansumugang_service.dto.user.ProtectorInfoUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;


@Entity
@Getter
@Setter
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
public abstract class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String username; // 유저 id

    private String password; // 비밀번호

    private String name; // 이름

    private String birthdate; // 사용자 생년월일

    private String telephone; // 사용자 전화번호

    private String usertype; // 환자, 보호자 구분

    private String authority; // 사용자 권한 : User / Admin

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt; // 유저 정보 업데이트 시간


    public User(
            String username,
            String password,
            String name,
            String birthdate,
            String telephone,
            String usertype,
            String authority
    ) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthdate = birthdate;
        this.telephone = telephone;
        this.authority = authority;
        this.usertype = usertype;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton((GrantedAuthority) () -> authority);
    }



    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }


}
