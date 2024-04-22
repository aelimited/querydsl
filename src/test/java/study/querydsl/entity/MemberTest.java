package study.querydsl.entity;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;


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
                    .select(member)
                    .from(member)
                    .where(member.userName.eq("MemberA"))
                    .fetchOne();

            if (findMember != null) {
                assertThat(findMember.getUserName()).isEqualTo("MemberA");
            } else {
                System.out.println(" = ");
            }
        }
    }

    @Test
    public void search() {
        if (queryFactory != null) {
            Member findMember = queryFactory
                    .select(member)
//                    .where(member.userName.eq("Member1")
//                            .and(member.age.eq(10)))
                    //and 조건을 파라미터로 처리 하는 방법
                    .where(member.userName.eq("member1"),
                            member.age.eq(10))
                    .fetchOne();

            assertThat(findMember.getUserName()).isEqualTo("Member1");
        }
    }

    @Test
    public void resultFetch() {
        if (queryFactory != null) {
            QueryResults<Member> results = queryFactory
                    .selectFrom(member)
                    .fetchResults();

            results.getTotal();
            List<Member> members = results.getResults();
        }
    }

    @Test
    public void sort() {
        if (queryFactory != null) {
            em.persist(new Member(null, 100));
            em.persist(new Member("member5", 100));
            em.persist(new Member("member6", 100));
            List<Member> result = queryFactory
                    .selectFrom(member)
                    .where(member.age.eq(100))
                    .orderBy(member.age.desc(),
                            member.userName.asc().nullsLast())
                    .fetch();
            Member member5 = result.get(0);
            Member member6 = result.get(1);
            Member memberNull = result.get(2);
            assertThat(member5.getUserName()).isEqualTo("member5");
            assertThat(member6.getUserName()).isEqualTo("member6");
            assertThat(memberNull.getUserName()).isNull();
        }
    }

    @Test
    public void paging() {
        if (queryFactory != null) {
            QueryResults<Member> queryResults = queryFactory
                    .selectFrom(member)
                    .orderBy(member.userName.desc())
                    .offset(1)
                    .limit(2)
                    .fetchResults();

            assertThat(queryResults.getTotal()).isEqualTo(4);
            assertThat(queryResults.getLimit()).isEqualTo(2);
            assertThat(queryResults.getOffset()).isEqualTo(1);
            assertThat(queryResults.getResults().size()).isEqualTo(2);
        }
    }
}