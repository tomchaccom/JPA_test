package test.jpa.study.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import test.jpa.study.domain.Like;
import test.jpa.study.domain.Post;
import test.jpa.study.domain.User;
import test.jpa.study.repository.LikeRepository;
import test.jpa.study.repository.PostRepository;
import test.jpa.study.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initData() {
        log.info("==== 더미 데이터 생성을 시작합니다 ====");

        // 유저 수를 10,000명으로 대폭 늘립니다 (N+1 쿼리를 1만 번 유발하기 위함)
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            User user = User.builder()
                    .username("user" + i)
                    .email("user" + i + "@test.com")
                    .build();
            users.add(user);
        }
        userRepository.saveAll(users);

        // 각 유저당 2개의 포스트를 작성하여 총 20,000개의 게시글을 만듭니다.
        List<Post> posts = new ArrayList<>();
        for (User user : users) {
            for (int j = 1; j <= 2; j++) {
                Post post = Post.builder()
                        .title(user.getUsername() + "의 게시글 " + j)
                        .content("게시글 내용입니다.")
                        .user(user)
                        .build();
                posts.add(post);
            }
        }
        // 대량 저장을 위해 루프
        posts.forEach(postRepository::save);

        // 좋아요 데이터도 조금 더 늘림
        for (int i = 0; i < 500; i++) {
            Like like = Like.builder()
                    .user(users.get(0))
                    .post(posts.get(i))
                    .build();
            likeRepository.save(like);
        }

        log.info("==== 더미 데이터 생성이 완료되었습니다 ====");
        log.info("생성된 다(N) 데이터 수: 유저 10000명, Post 20000개 로딩 완료");
    }
}
