package test.jpa.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeDto {
    private Long likeId;
    private String username; // N+1 쿼리를 유발하기 위해 User 에 접근
    private String postTitle; // N+1 쿼리를 유발하기 위해 Post 에 접근
}
