package test.jpa.study.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    // Like(N) : User(1) — 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Like(N) : Post(1) — 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
        // 양방향 동기화
        user.getLikes().add(this);
        post.getLikes().add(this);
    }
}
