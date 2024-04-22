package study.querydsl.entity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
@Transactional
@Commit
class MemberTest {
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @Test
    public void test() {
        Team teamA = new Team("TeamA");
        Team teamB = new Team("TeamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("MemberA");
        Member member2 = new Member("MemberA");
        Member member3 = new Member("MemberB");
        Member member4 = new Member("MemberB");

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();

        List<Member> members = em.createQuery("from Member", Member.class).getResultList();

        for (Member member : members) {
            System.out.println(member);
            System.out.println(member.getTeam());
        }
    }

    @Test
    public void startQuerydsl() {
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//        QMember m = new QMember("m"); //별칭을 사용하는 방법임
        if (queryFactory != null) {
            Member findMember = queryFactory
                    .select(QMember.member)
                    .from(QMember.member)
                    .where(QMember.member.userName.eq("MemberA"))
                    .fetchOne();

            if (findMember != null) {
                Assertions.assertThat(findMember.getUserName()).isEqualTo("MemberA");
            } else {
                System.out.println(" = ");
            }
        }
    }
}