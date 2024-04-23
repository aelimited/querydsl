package study.querydsl.entity;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;
import static study.querydsl.entity.QTeam.team;


@SpringBootTest
@Transactional
@Commit
class MemberTest {
    @PersistenceContext
    EntityManager em;

    JPAQueryFactory queryFactory;

    @PersistenceUnit
    EntityManagerFactory emf;

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

    @Test
    public void aggregation() throws Exception {
        if (queryFactory != null) {
            List<Tuple> result = queryFactory
                    .select(member.count(),
                            member.age.sum(),
                            member.age.avg(),
                            member.age.max(),
                            member.age.min())
                    .from(member)
                    .fetch();
            Tuple tuple = result.get(0);
            assertThat(tuple.get(member.count())).isEqualTo(4);
            assertThat(tuple.get(member.age.sum())).isEqualTo(100);
            assertThat(tuple.get(member.age.avg())).isEqualTo(25);
            assertThat(tuple.get(member.age.max())).isEqualTo(40);
            assertThat(tuple.get(member.age.min())).isEqualTo(10);
        }
    }

    @Test
    public void join() throws Exception {
        QMember member = QMember.member;
        QTeam team = QTeam.team;
        if (queryFactory != null) {
            List<Member> result = queryFactory
                    .selectFrom(member)
                    .join(member.team, team)
                    .where(team.name.eq("teamA"))
                    .fetch();

            assertThat(result)
                    .extracting("username")
                    .containsExactly("member1", "member2");
        }
    }

    @Test
    public void join_on_filtering() throws Exception {
        if (queryFactory != null) {
            List<Tuple> result = queryFactory
                    .select(member, team)
                    .from(member)
                    .leftJoin(member.team, team)
                    .on(team.name.eq("teamA"))
                    .fetch();

            for (Tuple tuple : result) {
                System.out.println("tuple = " + tuple);
            }
        }
    }

    @Test
    public void join_on_no_relation(){
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        if (queryFactory != null) {
            List<Tuple> result =queryFactory
                    .select(member, team)
                    .from(member)
                    .leftJoin(team).on(member.userName.eq(team.name))
                    .fetch();

            for (Tuple tuple : result){
                System.out.println("tuple = " + tuple);
            }
        }
    }

    @Test
    public void fetchJoinUse() throws Exception {
        em.flush();
        em.clear();

        if (queryFactory != null) {
            Member findMember = queryFactory
                    .selectFrom(member)
                    .join(member.team, team).fetchJoin()
                    .where(member.userName.eq("member1"))
                    .fetchOne();

            boolean loaded =
                    emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
            assertThat(loaded).as("페치 조인 적용").isTrue();
        }
    }



}