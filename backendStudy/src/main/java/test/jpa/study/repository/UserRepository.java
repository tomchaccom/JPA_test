package test.jpa.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.jpa.study.domain.User;

import java.util.List;

/**
 * Spring Data JPA를 이용한 UserRepository
 * 순수 JPA 방식(Post, Like Repository)과 달리 JpaRepository 인터페이스만 상속받으면
 * save, findById, findAll, delete 등의 기본적인 CRUD 메서드를 자동으로 제공받습니다.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA의 쿼리 메서드 기능
    // 메서드 이름만으로 JPQL 쿼리("SELECT u FROM User u WHERE u.username = :username")가 자동 생성됩니다.
    List<User> findByUsername(String username);

}
