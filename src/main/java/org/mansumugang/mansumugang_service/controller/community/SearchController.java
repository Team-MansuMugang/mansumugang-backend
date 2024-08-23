package org.mansumugang.mansumugang_service.controller.community;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mansumugang.mansumugang_service.domain.user.User;
import org.mansumugang.mansumugang_service.dto.community.search.Search;
import org.mansumugang.mansumugang_service.service.community.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class SearchController {

    private final SearchService searchService;

    @GetMapping()
    public ResponseEntity<Search.Response> searchPost(@AuthenticationPrincipal User user,
                                                      @RequestParam(name = "search") String searchContent,
                                                      @RequestParam(required = false, defaultValue = "1") int page
    ){

        Search.Response searchResult = searchService.searchPost(user, searchContent, page);

        return new ResponseEntity<>(searchResult, HttpStatus.OK);
    }
}
