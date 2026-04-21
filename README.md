# JPA N+1 프라블럼 스터디 프로젝트

이 프로젝트는 JPA(Java Persistence API)의 고질적인 성능 저하 원인인 **N+1 문제**를 의도적으로 발생시키고, 이를 `Fetch Join`과 `Entity Graph`를 통해 어떻게 최적화하고 해결하는지 체감하기 위한 학습용 서버입니다.

---

## 🛠️ 엔티티 관계도 (Entity Relationship Diagram)

```text
       [ User (사용자) ]
       +---------------+ 
       | PK: user_id   | 
       | username      |
       | email         |
       +-------+-------+
               | 1
               |
             N |          1 +------------------+ N
       [ Post (게시글) ] <----+   [ Like (좋아요) ]  |
       +---------------+    |   +--------------+ |
       | PK: post_id   |    |   | PK: like_id  | |
       | title         |    +---+ FK: post_id  | |
       | content       |        | FK: user_id  +-+
       | FK: user_id   |        +--------------+
       +---------------+
               |
               | (게시글 작성자 N:1)
               v
         [ User ] (참조)
```

- **User ↔ Post (1:N)**: 한 명의 유저가 여러 개의 게시글을 작성할 수 있습니다.
- **User ↔ Post (N:M)**: 유저는 여러 게시글에 좋아요를 누를 수 있고, 게시글도 여러 유저로부터 좋아요를 받을 수 있습니다. 이를 `Like`라는 중간 엔티티를 두어 `1:N`, `N:1` 관계로 풀어냈습니다.

---

## 📊 초기 세팅 데이터 (DataInitializer)

N+1 문제 발생 시 발생하는 막대한 지연 시간을 피부로 체감하기 위해, Spring Boot 서버 구동 시(ApplicationReadyEvent) 다음 규모의 더미 데이터가 ইন메모리(H2) DB에 자동 생성됩니다.

- 🧑‍💻 **User (유저):** **10,000 명** (`user1` ~ `user10000`)
- 📝 **Post (게시글):** **20,000 개** (각 유저 당 2개씩 작성)
- ❤️ **Like (좋아요):** **500 개** (1번 유저가 첫 500개의 글에 좋아요 클릭)

> N+1 환경에서 20,000개의 게시글을 노출할 경우, 각 게시글의 작성자(User - Proxy)를 가져오기 위해 캐시를 제외하고 **정확히 10,000번의 추가 `SELECT` 쿼리가 발생**하게끔 극단적으로 설계되었습니다.

---

## 🚀 API 명세 (API Specification)

해당 API들은 `http://localhost:8080/` 도메인 기준입니다.
모든 호출 시 콘솔(터미널)에 AOP를 통해 측정된 **수행 시간(ms)**이 기록됩니다.

### 1. Post 도메인 API
| Method | URI | 동작 설명 | 해결 방식 | 예상 동작 (쿼리 횟수) |
|---|---|---|---|---|
| **GET** | `/api/posts/nplus1` | 전체 게시글(+작성자) 조회 | **미적용** | 전체 조회 1번 + 개인조회 10,000번 (총 **10,001번**) |
| **GET** | `/api/posts/fetch-join` | 전체 게시글(+작성자) 조회 | `Fetch Join` | 성능 최적화: 단 한 번의 JOIN 쿼리 (**1번**) |
| **GET** | `/api/posts/entity-graph`| 전체 게시글(+작성자) 조회 | `EntityGraph` | 성능 최적화: 단 한 번의 JOIN 쿼리 (**1번**) |

### 2. Like 도메인 API (N:M 테스트)
| Method | URI | 동작 설명 | 해결 방식 | 예상 동작 (다중 N+1) |
|---|---|---|---|---|
| **GET** | `/api/likes/nplus1` | 전체 좋아요(+누른사람,글) | **미적용** | 좋아요 1번 + User확인 + Post확인 |
| **GET** | `/api/likes/fetch-join` | 전체 좋아요(+누른사람,글) | `Fetch Join` | 다중 연관관계 JOIN 쿼리 (**1번**) |

---

### 💡 응답 구조 예시 (Response Dto)

API 호출 시 반환되는 데이터 모델은 엔티티가 아닌 DTO로 매핑되어 제공됩니다.
(※ 매핑 중 영속성 컨텍스트를 터치하며 N+1 쿼리가 발생)

```json
[
  {
    "postId": 1,
    "title": "user1의 게시글 1",
    "content": "게시글 내용입니다.",
    "username": "user1"
  },
  {
    "postId": 2,
    "title": "user1의 게시글 2",
    "content": "게시글 내용입니다.",
    "username": "user1"
  }
  // ... 총 20,000개
]
```
