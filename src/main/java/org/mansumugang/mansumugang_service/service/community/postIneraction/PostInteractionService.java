package org.mansumugang.mansumugang_service.service.community.postIneraction;


import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostLike;
import org.mansumugang.mansumugang_service.domain.fcm.FcmToken;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;

import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.FcmTokenRepository;
import org.mansumugang.mansumugang_service.repository.PostBookmarkRepository;
import org.mansumugang.mansumugang_service.repository.PostLikeRepository;
import org.mansumugang.mansumugang_service.repository.PostRepository;
import org.mansumugang.mansumugang_service.service.fcm.FcmService;
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class PostInteractionService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final FcmTokenRepository fcmTokenRepository;

    private final FcmService fcmService;
    private final UserCommonService userCommonService;


    @Transactional
    public String postLikeToggle(User user, Long id){

        String responseMessage;

        Protector validProtector = userCommonService.findProtector(user);

        Post foundPost = postRepository.findById(id).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));


        PostLike foundLikeByProtectorAndPost = postLikeRepository.findByProtectorAndPost(validProtector, foundPost);

        if(foundLikeByProtectorAndPost == null){
            postLikeRepository.save(PostLike.of(foundPost, validProtector));

            sendLikeMessageToPostCreator(validProtector, foundPost);

            responseMessage = "게시물에 좋아요를 누르셨습니다.";

        }else {
            postLikeRepository.delete(foundLikeByProtectorAndPost);
            responseMessage = "게시물의 좋아요를 취소하셨습니다.";
        }

        return responseMessage;
    }


    private void sendLikeMessageToPostCreator(Protector validProtector, Post foundPost) {
        if (!validProtector.getUsername().equals(foundPost.getProtector().getUsername())){
            List<FcmToken> foundFcmTokens = fcmTokenRepository.findByProtectorId(foundPost.getProtector().getId());
            for (FcmToken fcmToken : foundFcmTokens) {
                Message message = Message.builder()
                        .putData("title", "만수무강")
                        .putData("body", validProtector.getNickname() + "님께서 회원님의 게시물을 좋아합니다!")
                        .setToken(fcmToken.getFcmToken())
                        .build();

                fcmService.sendMessage(message);
            }
        }
    }
}
