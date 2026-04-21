package test.jpa.study.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.jpa.study.domain.Post;
import test.jpa.study.dto.PostDto;
import test.jpa.study.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    /**
     * N+1 문제 유발
     * Post 전체를 조회한 후, User 프록시 객체의 username 필드에 접근하면서 N+1 쿼리가 발생함
     */
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        
        return posts.stream()
                .map(p -> new PostDto(
                        p.getId(), 
                        p.getTitle(), 
                        p.getContent(), 
                        p.getUser().getUsername() // Lazy Loading 프록시 초기화
                ))
                .collect(Collectors.toList());
    }

    /**
     * N+1 문제 해결: Fetch Join
     * Repository에서 쿼리 실행 시 조인하여 한 번에 데이터를 가져오므로 프록시 초기화 쿼리가 추가로 발생하지 않음
     */
    public List<PostDto> getAllPostsWithFetchJoin() {
        List<Post> posts = postRepository.findAllWithUserByFetchJoin();
        
        return posts.stream()
                .map(p -> new PostDto(
                        p.getId(), 
                        p.getTitle(), 
                        p.getContent(), 
                        p.getUser().getUsername() // 이미 로딩되어 있어서 쿼리 미발생
                ))
                .collect(Collectors.toList());
    }

    /**
     * N+1 문제 해결: Entity Graph
     * Repository에서 연관된 엔티티를 외부 옵션으로 지정해 함께 조회
     */
    public List<PostDto> getAllPostsWithEntityGraph() {
        List<Post> posts = postRepository.findAllWithUserByEntityGraph();
        
        return posts.stream()
                .map(p -> new PostDto(
                        p.getId(), 
                        p.getTitle(), 
                        p.getContent(), 
                        p.getUser().getUsername() // 이미 로딩되어 있어서 쿼리 미발생
                ))
                .collect(Collectors.toList());
    }
}
