package org.mansumugang.mansumugang_service.service.community.post;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.FileType;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.community.PostCategory;
import org.mansumugang.mansumugang_service.domain.community.PostImage;
import org.mansumugang.mansumugang_service.domain.community.PostLike;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.post.PostDelete;
import org.mansumugang.mansumugang_service.dto.community.post.PostInquiry;
import org.mansumugang.mansumugang_service.dto.community.post.PostSave;
import org.mansumugang.mansumugang_service.dto.community.post.PostUpdate;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.*;
import org.mansumugang.mansumugang_service.service.fileService.FileService;
import org.mansumugang.mansumugang_service.service.fileService.S3FileService;
import org.mansumugang.mansumugang_service.utils.ProfileChecker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final ProfileChecker profileChecker;

    private final FileService fileService;
    private final S3FileService s3FileService;

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final CommentRepository commentRepository;

    private final int PAGE_SIZE = 10; // 한 페이지에 보여줄 게시물 개수 -> 한 페이지당 10개.

    @Value("${file.upload.postImages.api}")
    private String postImageApiUrlPrefix;

    @Value("${file.upload.image.api}")
    private String imageApiUrl;

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

    public PostInquiry.PostListResponse getPosts(User user,String categoryCode, int pageNo){

        validateProtector(user);

        // 1번 페이지에서 최신 작성 게시물 순으로 정렬한다는 의미
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage;

        if (categoryCode != null && !categoryCode.isEmpty()){

            // 1. 쿼리파라미터로 받은 카테고리가 존재하는지 검증
            PostCategory foundPostCategory = postCategoryRepository.findByCategoryCode(categoryCode)
                    .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCategoryError));

            // 2. 카테고리로 게시물들 조회
            postPage = postRepository.findAllByPostCategoryId(foundPostCategory.getId(), pageable);

        } else{
            postPage = postRepository.findAll(pageable);
        }


        return PostInquiry.PostListResponse.fromPage(postPage);
    }

    public PostInquiry.PostDetailResponse getPostDetail(User user, Long id){

        Protector validProtector = validateProtector(user);

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

        // 6. 현재 게시물 상세정보에 접근한 유저가 좋아요를 누른지 판단
        boolean isLiked;

        PostLike foundPostLike = postLikeRepository.findByProtectorIdAndPostId(validProtector.getId(), foundPost.getId());

        isLiked = foundPostLike != null;

        // 보호자 프로필 이미지 저장경로 추가.
        return PostInquiry.PostDetailResponse.fromEntity(foundPost, foundPostImages, postImageApiUrlPrefix, imageApiUrl ,isLiked, likeCount, bookmarkCount, commentCount);
    }

    @Transactional
    public PostUpdate.Dto updatePost(User user, List<MultipartFile> imageFiles, PostUpdate.Request request){

        List<String> addedImages = new ArrayList<>();
        List<PostImage> postImages = new ArrayList<>();

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

        // 이미지 파일 추가, 삭제를 위한 추가 로직 구현.
        // 이미지 추가.
        savePostImage(imageFiles, addedImages, foundPost, postImages );

        // 이미지 제거 로직.
        if (request.getImageFilesToDelete() != null) {
            deletePostImages(request.getImageFilesToDelete());
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

        // 4. foundPostId로 게시물의 이미지들 조회 -> postImages에서 삭제, DB에서도 삭제
        deletePostImageFiles(foundPostId);

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

            String uniqueFileName = null;

            for (MultipartFile imageFile : imageFiles) {
                if (!fileService.checkImageFile(imageFile)){ // 이미지 파일이 null 또는 content-type 이 image 로 시작하지 않으면.
                    fileService.deleteImageFiles(addedImages);
                    throw new CustomErrorException(ErrorType.NoImageFileError);
                }

                if (profileChecker.checkActiveProfile("prod")) {
                    try {
                        uniqueFileName = s3FileService.savePostImageFile(imageFile);


                    } catch (IOException e) {
                        fileService.deleteImageFiles(addedImages);
                        throw new CustomErrorException(ErrorType.NoImageFileError);

                    } catch (Exception e) {
                        log.error(e.getMessage());
                        fileService.deleteImageFiles(addedImages);
                        throw new CustomErrorException(ErrorType.InternalServerError);
                    }
                }else {
                    try {
                        uniqueFileName = fileService.savePostImageFiles(imageFile);

                    } catch (NullPointerException e) {
                        fileService.deleteImageFiles(addedImages);
                        throw new CustomErrorException(ErrorType.NoImageFileError);

                    } catch (Exception e) {
                        log.error(e.getMessage());
                        fileService.deleteImageFiles(addedImages);
                        throw new CustomErrorException(ErrorType.InternalServerError);
                    }
                }

                PostImage savedPostImage = postImageRepository.save(PostImage.of(uniqueFileName, savedPost));
                addedImages.add(savedPostImage.getImageName());
                postImages.add(savedPostImage);
            }
        }
    }

    public void deletePostImages(List<String> imageFilesToDelete){
        if(!imageFilesToDelete.isEmpty()){

            for (String imageFileName : imageFilesToDelete) {

                PostImage foundPostImage = postImageRepository.findByImageName(imageFileName)
                        .orElseThrow(() -> new CustomErrorException(ErrorType.NoImageFileError));

                if (profileChecker.checkActiveProfile("prod")){
                    // profile 이 prod 라면 S3에서 파일 찾아서 삭제
                    s3FileService.deleteFileFromS3(imageFileName, FileType.POST_IMAGE);

                }else {
                    // 업로드된 이미지 파일 제거
                    fileService.deletePostImageFile(foundPostImage.getImageName());
                }

                // DB에 저장된 이미지 파일 정보 제거
                postImageRepository.delete(foundPostImage);
            }
        }

    }

    private void deletePostImageFiles(Long foundPostId) {
        List<PostImage> foundPostImages = postImageRepository.findPostImageByPostId(foundPostId);

        // 5. 이미지 파일 postImages에서 삭제
        for (PostImage foundPostImage : foundPostImages) {

            if (profileChecker.checkActiveProfile("prod")){

                s3FileService.deleteFileFromS3(foundPostImage.getImageName(), FileType.POST_IMAGE);

            }else {
                fileService.deletePostImageFile(foundPostImage.getImageName());
            }
        }
    }


}
