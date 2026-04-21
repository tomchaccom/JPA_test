package test.jpa.study.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import test.jpa.study.domain.Like;

import java.util.List;
import java.util.Optional;

@Repository
public class LikeRepository {

    @PersistenceContext
    private EntityManager em;

    // 저장
    public Like save(Like like) {
        em.persist(like);
        return like;
    }

    // 단건 조회
    public Optional<Like> findById(Long id) {
        return Optional.ofNullable(em.find(Like.class, id));
    }

    // 전체 조회 (N+1 문제 발생 지점)
    public List<Like> findAll() {
        return em.createQuery("SELECT l FROM Like l", Like.class)
                .getResultList();
    }

    // Fetch Join으로 User, Post까지 한번에 조회
    public List<Like> findAllWithUserAndPostByFetchJoin() {
        return em.createQuery(
                "SELECT l FROM Like l JOIN FETCH l.user JOIN FETCH l.post", Like.class)
                .getResultList();
    }

    // 특정 유저의 좋아요 목록
    public List<Like> findByUserId(Long userId) {
        return em.createQuery(
                "SELECT l FROM Like l WHERE l.user.id = :userId", Like.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    // 특정 게시글의 좋아요 수
    public Long countByPostId(Long postId) {
        return em.createQuery(
                "SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId", Long.class)
                .setParameter("postId", postId)
                .getSingleResult();
    }

    // 삭제
    public void delete(Like like) {
        em.remove(like);
    }
}
