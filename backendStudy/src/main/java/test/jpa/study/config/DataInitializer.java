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

        // 100명의 유저를 생성합니다.
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            User user = User.builder()
                    .username("user" + i)
                    .email("user" + i + "@test.com")
                    .build();
            users.add(user);
        }
        userRepository.saveAll(users);

        // 일대다 무결성을 위해 각 유저가 10개의 게시물을 작성하도록 합니다. (총 1000개의 Post)
        // 극단적인 N+1 확인을 원한다면 유저 1000명 / 각 1개 포스트도 좋지만, 현실적인 비율인 유저1:포스트10으로 설정합니다.
        List<Post> posts = new ArrayList<>();
        for (User user : users) {
            for (int j = 1; j <= 10; j++) {
                Post post = Post.builder()
                        .title(user.getUsername() + "의 게시글 " + j)
                        .content("게시글 내용입니다.")
                        .user(user)
                        .build();
                posts.add(post);
            }
        }
        // Save using persist via a loop since PostRepository has no saveAll in pure JPA implementation
        posts.forEach(postRepository::save);

        // N:M Like 더미 데이터도 100개(글 100개에 대해 1번 유저가 좋아요) 정도 생성합니다.
        for (int i = 0; i < 100; i++) {
            Like like = Like.builder()
                    .user(users.get(0)) // 1번 유저가
                    .post(posts.get(i)) // 첫 100개의 글에 좋아요
                    .build();
            likeRepository.save(like);
        }

        log.info("==== 더미 데이터 생성이 완료되었습니다 ====");
        log.info("생성된 다(N) 데이터 수: Post 1000개 완료");
    }
}
