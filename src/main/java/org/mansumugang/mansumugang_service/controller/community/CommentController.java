package org.mansumugang.mansumugang_service.controller.community;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentDelete;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentInquiry;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentSave;
import org.mansumugang.mansumugang_service.dto.community.comment.CommentUpdate;
import org.mansumugang.mansumugang_service.service.community.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/comment")
public class CommentController {

    private final CommentService commentService;

    /**
     * request에 postId를 댓글 내용과 함께 담은 이유 : ReuquestBody의 단일 필드에 대한 역직렬화 문제 해결.
     */

    @PostMapping("/save")
    public ResponseEntity<CommentSave.Response> saveComment(@AuthenticationPrincipal User user,
                                                            @Valid @RequestBody CommentSave.Request request
    ){
        CommentSave.Dto dto = commentService.saveComment(user, request);

        return new ResponseEntity<>(CommentSave.Response.createNewResponse(dto), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<CommentInquiry.Response> getCommentList(@RequestParam(value = "cursor", required = false) Long cursor,
                                                                  @RequestParam(value = "postId") Long postId
    ){

        CommentInquiry.Response commentListResponse = commentService.getCommentList(cursor, postId);

        return new ResponseEntity<>(commentListResponse, HttpStatus.OK);

    }

    @PatchMapping()
    public ResponseEntity<CommentUpdate.Response> updateComment(@AuthenticationPrincipal User user,
                                                                @Valid @RequestBody CommentUpdate.Request request
    ){
        CommentUpdate.Dto dto = commentService.updateComment(user, request);

        return new ResponseEntity<>(CommentUpdate.Response.createNewResponse(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<CommentDelete.Response> deleteComment(@AuthenticationPrincipal User user,
                                                                @PathVariable(name = "id") Long commentId){

        CommentDelete.Dto dto = commentService.deleteComment(user, commentId);

        return new ResponseEntity<>(CommentDelete.Response.CreateNewResponse(dto),HttpStatus.CREATED);
    }

}
