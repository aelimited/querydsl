package study.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class MemberTeamDto {
    private Long memberId;
    private String memberName;
    private int age;
    private Long teamId;
    private String teamName;

    @QueryProjection

    public MemberTeamDto(Long memberId, String memberName, int age, Long teamId, String teamName) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.age = age;
        this.teamId = teamId;
        this.teamName = teamName;
    }
}
