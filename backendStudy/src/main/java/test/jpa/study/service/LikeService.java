package test.jpa.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.jpa.study.domain.Like;
import test.jpa.study.dto.LikeDto;
import test.jpa.study.repository.LikeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {

    private final LikeRepository likeRepository;

    /**
     * N+1 문제 유발
     * Like 엔티티 조회 후 User와 Post에 각각 접근하여 다량의 쿼리가 발생함
     */
    public List<LikeDto> getAllLikes() {
        List<Like> likes = likeRepository.findAll();

        return likes.stream()
                .map(l -> new LikeDto(
                        l.getId(),
                        l.getUser().getUsername(), // User Proxy 초기화
                        l.getPost().getTitle() // Post Proxy 초기화
                ))
                .collect(Collectors.toList());
    }

    /**
     * N+1 문제 해결: Fetch Join
     */
    public List<LikeDto> getAllLikesWithFetchJoin() {
        List<Like> likes = likeRepository.findAllWithUserAndPostByFetchJoin();

        return likes.stream()
                .map(l -> new LikeDto(
                        l.getId(),
                        l.getUser().getUsername(),
                        l.getPost().getTitle()
                ))
                .collect(Collectors.toList());
    }
}
