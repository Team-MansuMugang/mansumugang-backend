package org.mansumugang.mansumugang_service.service.community.post;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.constant.InternalErrorType;
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
import org.mansumugang.mansumugang_service.exception.InternalErrorException;
import org.mansumugang.mansumugang_service.repository.*;
import org.mansumugang.mansumugang_service.service.file.FileService;
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${file.upload.image.api}")
    private String imageApiUrl;

    private final FileService fileService;
    private final UserCommonService userCommonService;

    private final PostRepository postRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;
    private final CommentRepository commentRepository;

    private final int PAGE_SIZE = 10;

    @Transactional
    public PostSave.Dto savePostImage(User user, PostSave.Request request, List<MultipartFile> imageFiles){


        List<String> addedImages = new ArrayList<>();
        List<PostImage> postImages = new ArrayList<>();

        Protector validProtector = userCommonService.findProtector(user);

        PostCategory foundPostCategory = postCategoryRepository.findByCategoryCode(request.getCategoryCode())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCategoryError));

        Post savedPost = postRepository.save(Post.of(request, foundPostCategory ,validProtector));

        savePostImage(imageFiles, addedImages, savedPost, postImages);

        return PostSave.Dto.fromEntity(savedPost);
    }

    public PostInquiry.PostListResponse getPosts(User user,String categoryCode, int pageNo){

        userCommonService.findProtector(user);

        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage;

        if (categoryCode != null && !categoryCode.isEmpty()){

            PostCategory foundPostCategory = postCategoryRepository.findByCategoryCode(categoryCode)
                    .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCategoryError));

            postPage = postRepository.findAllByPostCategoryId(foundPostCategory.getId(), pageable);

        } else{
            postPage = postRepository.findAll(pageable);
        }


        return PostInquiry.PostListResponse.fromPage(postPage);
    }

    public PostInquiry.PostDetailResponse getPostDetail(User user, Long id){

        Protector validProtector = userCommonService.findProtector(user);

        Post foundPost = postRepository.findById(id).orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        List<PostImage> foundPostImages = postImageRepository.findPostImageByPostId(foundPost.getId());

        Long likeCount = postLikeRepository.countByPostId(foundPost.getId());

        Long bookmarkCount = postBookmarkRepository.countByPostId(foundPost.getId());

        Long commentCount = commentRepository.countByPostId(foundPost.getId());

        boolean isLiked;

        PostLike foundPostLike = postLikeRepository.findByProtectorIdAndPostId(validProtector.getId(), foundPost.getId());

        isLiked = foundPostLike != null;

        // 보호자 프로필 이미지 저장경로 추가.
        return PostInquiry.PostDetailResponse.fromEntity(foundPost, foundPostImages, imageApiUrl, imageApiUrl ,isLiked, likeCount, bookmarkCount, commentCount);
    }

    @Transactional
    public PostUpdate.Dto updatePost(User user, List<MultipartFile> imageFiles, PostUpdate.Request request){

        List<String> addedImages = new ArrayList<>();
        List<String> imageFilesToDelete = new ArrayList<>();
        List<PostImage> postImages = new ArrayList<>();

        Protector validProtector = userCommonService.findProtector(user);

        Post foundPost = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        PostCategory foundPostCategory = postCategoryRepository.findByCategoryCode(request.getCategoryCode())
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchCategoryError));

        if (!validProtector.getUsername().equals(foundPost.getProtector().getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfThePost);
        }

        if (imageFiles != null){
            List<PostImage> foundPostImages = postImageRepository.findPostImageByPostId(foundPost.getId());
            for (PostImage foundPostImage : foundPostImages) {
                imageFilesToDelete.add(foundPostImage.getImageName());
            }
            deletePostImages(imageFilesToDelete);
        }

        savePostImage(imageFiles, addedImages, foundPost, postImages );

        foundPost.update(request, foundPostCategory);


        return PostUpdate.Dto.fromEntity(foundPost);
    }

    @Transactional
    public PostDelete.Dto deletePost(User user, Long postId){

        Protector validProtector = userCommonService.findProtector(user);

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new CustomErrorException(ErrorType.NoSuchPostError));

        Long foundPostId = foundPost.getId();
        String foundPostTitle = foundPost.getTitle();

        if (!validProtector.getUsername().equals(foundPost.getProtector().getUsername())){
            throw new CustomErrorException(ErrorType.NotTheAuthorOfThePost);
        }

        deletePostImageFiles(foundPostId);

        postRepository.delete(foundPost);

        return PostDelete.Dto.fromEntity(foundPostId, foundPostTitle);
    }


    private void savePostImage(List<MultipartFile> imageFiles, List<String> addedImages, Post savedPost, List<PostImage> postImages) {

        if (imageFiles != null){

            for (MultipartFile imageFile : imageFiles) {
                try {
                    String uniqueFileName = fileService.saveImageFile(imageFile);
                    PostImage savedPostImage = postImageRepository.save(PostImage.of(uniqueFileName, savedPost));
                    addedImages.add(savedPostImage.getImageName());
                    postImages.add(savedPostImage);

                } catch (InternalErrorException e) {
                    if(e.getInternalErrorType() == InternalErrorType.EmptyFileError) {
                        throw new CustomErrorException(ErrorType.NoImageFileError);
                    }

                    if(e.getInternalErrorType() == InternalErrorType.InvalidFileExtension) {
                        throw new CustomErrorException(ErrorType.InvalidImageFileExtension);
                    }

                    if(e.getInternalErrorType() == InternalErrorType.FileSaveError) {
                        fileService.deleteImageFiles(addedImages);
                        throw new CustomErrorException(ErrorType.InternalServerError);
                    }
                }


            }
        }
    }

    public void deletePostImages(List<String> imageFilesToDelete){
        if(!imageFilesToDelete.isEmpty()){

            for (String imageFileName : imageFilesToDelete) {

                PostImage foundPostImage = postImageRepository.findByImageName(imageFileName)
                        .orElseThrow(() -> new CustomErrorException(ErrorType.NoImageFileError));

                fileService.deleteImageFile(imageFileName);
                postImageRepository.delete(foundPostImage);
            }
        }

    }


    private void deletePostImageFiles(Long foundPostId) {
        List<PostImage> foundPostImages = postImageRepository.findPostImageByPostId(foundPostId);

        for (PostImage foundPostImage : foundPostImages) {
            fileService.deleteImageFile(foundPostImage.getImageName());
        }
    }


}
