package test.jpa.study.repository;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import test.jpa.study.domain.Post;

import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

    @PersistenceContext
    private EntityManager em;

    // 저장
    public Post save(Post post) {
        em.persist(post);
        return post;
    }

    // 단건 조회
    public Optional<Post> findById(Long id) {
        return Optional.ofNullable(em.find(Post.class, id));
    }

    // 전체 조회 (N+1 문제 발생 지점)
    public List<Post> findAll() {
        return em.createQuery("SELECT p FROM Post p", Post.class)
                .getResultList();
    }

    // Fetch Join으로 User까지 한번에 조회 (N+1 해결 방법 1)
    public List<Post> findAllWithUserByFetchJoin() {
        return em.createQuery(
                "SELECT p FROM Post p JOIN FETCH p.user", Post.class)
                .getResultList();
    }

    // EntityGraph로 User까지 한번에 조회 (N+1 해결 방법 3)
    public List<Post> findAllWithUserByEntityGraph() {
        EntityGraph<Post> entityGraph = em.createEntityGraph(Post.class);
        entityGraph.addAttributeNodes("user");

        return em.createQuery("SELECT p FROM Post p", Post.class)
                .setHint("jakarta.persistence.fetchgraph", entityGraph)
                .getResultList();
    }

    // 특정 유저의 게시글 조회
    public List<Post> findByUserId(Long userId) {
        return em.createQuery(
                "SELECT p FROM Post p WHERE p.user.id = :userId", Post.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    // 삭제
    public void delete(Post post) {
        em.remove(post);
    }
}
