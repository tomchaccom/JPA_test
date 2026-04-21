package test.jpa.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostDto {
    private Long postId;
    private String title;
    private String content;
    private String username; // N+1 쿼리를 유발하기 위해 엔티티의 연관 필드에 접근
}
