package test.jpa.study.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.jpa.study.dto.LikeDto;
import test.jpa.study.service.LikeService;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // 1. 다중 N+1 문제 발생 API (User, Post 각각 초기화)
    @GetMapping("/nplus1")
    public List<LikeDto> getLikesNPlus1() {
        return likeService.getAllLikes();
    }

    // 2. N+1 해결 - Fetch Join 적용 API
    @GetMapping("/fetch-join")
    public List<LikeDto> getLikesFetchJoin() {
        return likeService.getAllLikesWithFetchJoin();
    }
}
