package org.mansumugang.mansumugang_service.controller.community;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.post.PostDelete;
import org.mansumugang.mansumugang_service.dto.community.post.PostInquiry;
import org.mansumugang.mansumugang_service.dto.community.post.PostSave;
import org.mansumugang.mansumugang_service.dto.community.post.PostUpdate;
import org.mansumugang.mansumugang_service.service.community.CommentService;
import org.mansumugang.mansumugang_service.service.community.post.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;
    @PostMapping("/save")
    public ResponseEntity<PostSave.Response> savePost(
            @AuthenticationPrincipal User user,
            @Valid @RequestPart(name = "post") PostSave.Request request,
            @RequestPart(name = "imageFiles", required = false)List<MultipartFile> imageFiles
    ){

        PostSave.Dto dto = postService.savePostImage(user, request, imageFiles);

        return new ResponseEntity<>(PostSave.Response.createNewResponse(dto), HttpStatus.CREATED);

    }

    @GetMapping()
    public ResponseEntity<PostInquiry.PostListResponse> getPost(@AuthenticationPrincipal User user,
                                                                @RequestParam(required = false) String categoryCode,
                                                                @RequestParam(required = false, defaultValue = "1") int page){

        PostInquiry.PostListResponse foundPosts = postService.getPosts(user, categoryCode, page);

        return new ResponseEntity<>(foundPosts, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<PostInquiry.PostDetailResponse> getPost(@AuthenticationPrincipal User user,
                                                                  @PathVariable(name = "id") Long id){

        PostInquiry.PostDetailResponse foundPostDetail = postService.getPostDetail(user, id);

        return new ResponseEntity<>(foundPostDetail, HttpStatus.OK);

    }

    @PatchMapping()
    public ResponseEntity<PostUpdate.Response> updatePost(@AuthenticationPrincipal User user,
                                                          @RequestPart(name = "imageFiles", required = false) List<MultipartFile> imageFiles,
                                                          @Valid @RequestPart(name = "post") PostUpdate.Request request
    ){

        PostUpdate.Dto dto = postService.updatePost(user, imageFiles, request);

        return new ResponseEntity<>(PostUpdate.Response.createNewResponse(dto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<PostDelete.Response> deletePost(@AuthenticationPrincipal User user,
                                                          @PathVariable(name = "id") Long postId){

        PostDelete.Dto dto = postService.deletePost(user, postId);

        return new ResponseEntity<>(PostDelete.Response.createNewResponse(dto), HttpStatus.CREATED);
    }

}
