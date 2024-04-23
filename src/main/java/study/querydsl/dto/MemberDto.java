package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberDto {
    private String name;
    private int age;

    public MemberDto() {
    }

    @QueryProjection //dto q파일 생성
    public MemberDto(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
