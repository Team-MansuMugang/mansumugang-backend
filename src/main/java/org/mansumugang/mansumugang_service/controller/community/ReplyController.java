package org.mansumugang.mansumugang_service.controller.community;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.reply.ReplySave;
import org.mansumugang.mansumugang_service.service.community.ReplyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/comment/reply")
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/save")
    public ResponseEntity<ReplySave.Response> saveReply(@AuthenticationPrincipal User user,
                                                        @Valid @RequestBody ReplySave.Request request
                                                        ){

        ReplySave.Dto dto = replyService.saveReply(user, request);

        return new ResponseEntity<>(ReplySave.Response.createNewResponse(dto), HttpStatus.CREATED);
    }
}
