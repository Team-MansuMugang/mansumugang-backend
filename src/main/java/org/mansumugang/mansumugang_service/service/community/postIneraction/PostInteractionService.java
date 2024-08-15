package org.mansumugang.mansumugang_service.service.community.postIneraction;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostBookmark;
import org.mansumugang.mansumugang_service.domain.community.PostLike;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;

import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PostBookmarkRepository;
import org.mansumugang.mansumugang_service.repository.PostLikeRepository;
import org.mansumugang.mansumugang_service.repository.PostRepository;
import org.springframework.stereotype.Service;



@Service
@Slf4j
@RequiredArgsConstructor
public class PostInteractionService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;


    @Transactional
    public String postLikeToggle(User user, Long id){

        String message;

        // 1. user가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 게시물 고유번호로 게시물 조회 후 좋아요 토글 가능.
        Post foundPost = postRepository.findById(id).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        // 3. 유저의 아이디와 게시물 번호로 등록된 postLike가 있는지 확인. 있으면 -> 삭제, 없으면 -> 저장

        PostLike foundLikeByProtectorAndPost = postLikeRepository.findByProtectorAndPost(validProtector, foundPost);

        if(foundLikeByProtectorAndPost == null){
            postLikeRepository.save(PostLike.of(foundPost, validProtector));
            message = "게시물에 좋아요를 누르셨습니다.";

        }else {
            postLikeRepository.delete(foundLikeByProtectorAndPost);
            message = "게시물의 좋아요를 취소하셨습니다.";
        }

        return message;
    }

    @Transactional
    public String postBookmarkToggle(User user, Long id){

        String message;

        // 1. user가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. 게시물 고유번호로 게시물 조회 후 좋아요 토글 가능.
        Post foundPost = postRepository.findById(id).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        // 3. 유저의 아이디와 게시물 번호로 등록된 postLike가 있는지 확인. 있으면 -> 삭제, 없으면 -> 저장

        PostBookmark foundBookmarkByProtectorAndPost = postBookmarkRepository.findByProtectorAndPost(validProtector, foundPost);

        if(foundBookmarkByProtectorAndPost == null){
            postBookmarkRepository.save(PostBookmark.of(foundPost, validProtector));
            message = "게시물을 스크랩하셨습니다.";

        }else {
            postBookmarkRepository.delete(foundBookmarkByProtectorAndPost);
            message = "게시물의 스크랩을 취소하셨습니다.";
        }

        return message;
    }



    private Protector validateProtector(User user){
        log.info("AuthenticationPrincipal 로 받은 유저 객체가 보호자 객체인지 검증 시작");
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Protector) {

            log.info("보호자 객체 검증 완료");
            return (Protector) user;
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }
}
