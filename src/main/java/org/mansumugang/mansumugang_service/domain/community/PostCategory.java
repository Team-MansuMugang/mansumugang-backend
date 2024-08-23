package org.mansumugang.mansumugang_service.domain.community;


import jakarta.persistence.*;
import lombok.*;
import org.mansumugang.mansumugang_service.dto.community.post.PostUpdate;

@Entity
@Getter
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryCode;

    private String categoryName;

    public static PostCategory of(
            String categoryCode,
            String categoryName
    ){
        return PostCategory.builder()
                .categoryCode(categoryCode)
                .categoryName(categoryName)
                .build();
    }

}
