package test.jpa.study.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.jpa.study.dto.PostDto;
import test.jpa.study.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 1. N+1 문제 발생 API
    @GetMapping("/nplus1")
    public List<PostDto> getPostsNPlus1() {
        return postService.getAllPosts();
    }

    // 2. N+1 해결 - Fetch Join 적용 API
    @GetMapping("/fetch-join")
    public List<PostDto> getPostsFetchJoin() {
        return postService.getAllPostsWithFetchJoin();
    }

    // 3. N+1 해결 - Entity Graph 적용 API
    @GetMapping("/entity-graph")
    public List<PostDto> getPostsEntityGraph() {
        return postService.getAllPostsWithEntityGraph();
    }
}
