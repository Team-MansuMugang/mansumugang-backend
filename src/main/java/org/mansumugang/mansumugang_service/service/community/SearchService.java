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
import org.mansumugang.mansumugang_service.service.user.UserCommonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final int PAGE_SIZE = 10;

    private final PostRepository postRepository;
    private final UserCommonService userCommonService;

    public Search.Response searchPost(User user, String searchContent, int pageNo){

        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage;

        userCommonService.findProtector(user);

        if (searchContent.strip().length() < 2 ){
            throw  new CustomErrorException(ErrorType.InvalidQueryError);
        }

        postPage =  postRepository.findByTitleOrContentContaining(searchContent, pageable);

        return Search.Response.fromPage(postPage);

    }
}
