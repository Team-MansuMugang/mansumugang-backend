package org.mansumugang.mansumugang_service.service.community.post;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostCategory;
import org.mansumugang.mansumugang_service.domain.community.PostImage;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.post.PostDelete;
import org.mansumugang.mansumugang_service.dto.community.post.PostInquiry;
import org.mansumugang.mansumugang_service.dto.community.post.PostSave;
import org.mansumugang.mansumugang_service.dto.community.post.PostUpdate;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.*;
import org.mansumugang.mansumugang_service.service.fileService.FileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final FileService fileService;

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final CommentRepository commentRepository;

    private final int PAGE_SIZE = 10; // 한 페이지에 보여줄 게시물 개수 -> 한 페이지당 10개.

    @Transactional
    public PostSave.Dto savePostImage(User user, PostSave.Request request, List<MultipartFile> imageFiles){

        log.info("서비스 호출");

        List<String> addedImages = new ArrayList<>();
        List<PostImage> postImages = new ArrayList<>();

        // 1. 받은 User 객체가 보호자 객체인지 확인하기.
        Protector validProtector = validateProtector(user);

        // 2. 지정할 게시물의 카테고리 찾기
        PostCategory foundPostCategory = postCategoryRepository.findByCategoryCode(request.getCategoryCode())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCategoryError));

        // 3. 게시물 저장
        Post savedPost = postRepository.save(Post.of(request, foundPostCategory ,validProtector));

        // 4. 이미지 파일 저장.
        savePostImage(imageFiles, addedImages, savedPost, postImages);

        return PostSave.Dto.fromEntity(savedPost);
    }

    public PostInquiry.PostListResponse getPosts(int pageNo){

        // 1번 페이지에서 최신 작성 게시물 순으로 정렬한다는 의미
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage = postRepository.findAll(pageable);


        return PostInquiry.PostListResponse.fromPage(postPage);
    }

    public PostInquiry.PostDetailResponse getPostDetail(Long id){

        // 1. 경로변수로 받은 id로 게시물 조회 -> 없으면 예외 처리
        Post foundPost = postRepository.findById(id).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        // 2. 찾아진 게시물로 게시물 이미지 조회 -> 게시물 저장시 이미지 저장이 필수가 아니기 때문에 빈 리스트일 수 있음.
        List<PostImage> foundPostImages = postImageRepository.findPostImageByPostId(foundPost.getId());

        // 3. 찾아진 게시물로 좋아요 수 카운트 -> 최솟값 : 0
        Long likeCount = postLikeRepository.countByPostId(foundPost.getId());

        // 4.찾아진 게시물로 북마크 수 카운트 -> 최솟값 : 0
        Long bookmarkCount = postBookmarkRepository.countByPostId(foundPost.getId());

        // 5. 찾아진 게시물로 댓글 수 카운트 -> 최솟값 : 0
        Long commentCount = commentRepository.countByPostId(foundPost.getId());

        // 6. 찾아진 게시물에서 나온 모든 댓글들
//        commentRepository.findByPostId

        return PostInquiry.PostDetailResponse.fromEntity(foundPost, foundPostImages, likeCount, bookmarkCount, commentCount);
    }

    @Transactional
    public PostUpdate.Dto updatePost(User user, PostUpdate.Request request){

        // 1. 넘겨받은 user객체가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. request에서 postId->게시물 조회 없으면 예외처리.
        Post foundPost = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        // 3. request 에서 categoryCode 로 카테고리 검색 없으면 예외처리.
        PostCategory foundPostCategory = postCategoryRepository.findByCategoryCode(request.getCategoryCode())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCategoryError));

        // 3. user.getUsername 과 게시물 작성자의 아이디가 같은지 검증
        if (!validProtector.getUsername().equals(foundPost.getProtector().getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfThePost);
        }

        // 4. 게시물 수정 시작
        foundPost.update(request, foundPostCategory);


        return PostUpdate.Dto.fromEntity(foundPost);
    }

    @Transactional
    public PostDelete.Dto deletePost(User user, Long postId){

        // 1. 넘겨받은 user객체가 보호자 객체인지 검증
        Protector validProtector = validateProtector(user);

        // 2. postId->게시물 조회 없으면 예외처리.
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        Long foundPostId = foundPost.getId();
        String foundPostTitle = foundPost.getTitle();

        // 3. user.getUsername 과 게시물 작성자의 아이디가 같은지 검증
        if (!validProtector.getUsername().equals(foundPost.getProtector().getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfThePost);
        }

        // 5. 게시물 삭제
        postRepository.delete(foundPost);

        return PostDelete.Dto.fromEntity(foundPostId, foundPostTitle);
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

    private void savePostImage(List<MultipartFile> imageFiles, List<String> addedImages, Post savedPost, List<PostImage> postImages) {
        if (imageFiles != null){

            for (MultipartFile imageFile : imageFiles) {
                if (!fileService.checkImageFile(imageFile)){ // 이미지 파일이 null 또는 content-type 이 image 로 시작하지 않으면.
                    fileService.deleteImageFiles(addedImages);
                    throw  new CustomErrorException(ErrorType.NoImageFileError);
                }

                try {
                    String uniqueFileName = fileService.savePostImageFiles(imageFile);
                    PostImage savedPostImage = postImageRepository.save(PostImage.of(uniqueFileName, savedPost));
                    addedImages.add(savedPostImage.getImageName());
                    postImages.add(savedPostImage);

                } catch (NullPointerException e) {
                    fileService.deleteImageFiles(addedImages);
                    throw new CustomErrorException(ErrorType.NoImageFileError);

                } catch (Exception e){
                    log.error(e.getMessage());
                    fileService.deleteImageFiles(addedImages);
                    throw new CustomErrorException(ErrorType.InternalServerError);
                }
            }
        }
    }
}
