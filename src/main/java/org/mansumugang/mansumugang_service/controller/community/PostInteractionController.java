package org.mansumugang.mansumugang_service.controller.community;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.postInteraction.PostInteraction;
import org.mansumugang.mansumugang_service.service.community.postIneraction.PostInteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{id}")
public class PostInteractionController {

    private final PostInteractionService postInteractionService;

    @PostMapping("/like")
    public ResponseEntity<PostInteraction.PostLikeResponse> postLikeToggle(@AuthenticationPrincipal User user,
                                                          @PathVariable(name = "id")Long id
    ){
        String responseMessage = postInteractionService.postLikeToggle(user, id);

        return ResponseEntity.ok(PostInteraction.PostLikeResponse.createNewResponse(responseMessage));
    }

    @PostMapping("/bookmark")
    public ResponseEntity<PostInteraction.PostBookmarkResponse> postBookmarkToggle(@AuthenticationPrincipal User user,
                                                                               @PathVariable(name = "id")Long id
    ){
        String message = postInteractionService.postBookmarkToggle(user, id);

        return ResponseEntity.ok(PostInteraction.PostBookmarkResponse.createNewResponse(message));
    }

}
