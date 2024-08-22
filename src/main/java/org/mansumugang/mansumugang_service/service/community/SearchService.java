package org.mansumugang.mansumugang_service.service.community;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.constant.ErrorType;
import org.mansumugang.mansumugang_service.domain.community.Post;
import org.mansumugang.mansumugang_service.domain.user.Protector;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.search.Search;
import org.mansumugang.mansumugang_service.exception.CustomErrorException;
import org.mansumugang.mansumugang_service.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final int PAGE_SIZE = 10; // 한 페이지에 보여줄 게시물 개수 -> 한 페이지당 10개.

    private final PostRepository postRepository;

    public Search.Response searchPost(User user, String searchContent, int pageNo){

        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage;

        // 1. user 가 보호자 객체인지 검증.
         validateProtector(user);

        // 2. content 가 두글자 이상인지 검증
        if (searchContent.strip().length() < 2 ){
            throw  new CustomErrorException(ErrorType.InvalidQueryError);
        }

        // 게시물 제목이나 본문 내용에서 content 와 동일한 문자를 포함하는 게시물 리스트 반환
        postPage =  postRepository.findByTitleOrContentContaining(searchContent, pageable);

        return Search.Response.fromPage(postPage);

    }

    private Protector validateProtector(User user){
        log.info("AuthenticationPrincipal 로 받은 유저 객체가 보호자 객체인지 검증 시작");
        if (user == null) {
            throw new CustomErrorException(ErrorType.UserNotFoundError);
        }

        if (user instanceof Protector) {

            log.info("보호자 객체 검증 완료");

            return (Protector) user ;
        }

        throw new CustomErrorException(ErrorType.AccessDeniedError);
    }
}
