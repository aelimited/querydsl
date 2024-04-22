package study.querydsl.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private long id;
    private String userName;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public Member(String member2, int i) {
    }

    public Member(String UserName) {
        this.userName = UserName;
    }

    public Member(String UserName, int age, Team team) {
        this.userName = UserName;
        this.age = age;
        this.team = team;
    }

    public Member() {

    }

    public void changTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }
}
