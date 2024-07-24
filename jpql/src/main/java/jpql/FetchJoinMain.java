package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;

public class FetchJoinMain {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql")) {
            EntityManager em = null;
            EntityTransaction tx = null;
            try {
                em = emf.createEntityManager();
                tx = em.getTransaction();
                tx.begin();

                Team teamA = new Team();
                teamA.setName("teamA");
                Team teamB = new Team();
                teamB.setName("teamB");
                Team teamC = new Team();
                teamC.setName("teamC");

                em.persist(teamA);
                em.persist(teamB);
                em.persist(teamC);


                Member member1 = new Member();
                member1.setUsername("회원1");
                member1.changeTeam(teamA);

                Member member2 = new Member();
                member2.setUsername("회원2");
                member2.changeTeam(teamA);

                Member member3 = new Member();
                member3.setUsername("회원3");
                member3.changeTeam(teamC);

                em.persist(member1);
                em.persist(member2);
                em.persist(member3);

                String query = "select m from Member m";

                List<Member> resultList = em.createQuery(query, Member.class).getResultList();
                for (Member member : resultList) {
                    // 현재는 지연 로딩이라 Team 객체 내부를 참조할 때마다 영속성 컨텍스트 조회 및 없으면 query 발생
                    System.out.println("username = " + member.getUsername() + ", " + "teamName = " + member.getTeam().getName());
                    // 회원1, teamA(SQL)
                    // 회원2, teamA(1차캐시)
                    // 회원3, teamB(SQL)
                    // =========== SQL 2번 조회 ==========

                    // 1+N 문제
                    // 회원 100명 조회하는 쿼리(1) 때문에 100개(N)의 데이터에 대한 쿼리를 날란다
                }
                // fetch join 테스트 - 단일 값 연관관계
                em.clear();
                String fetchQuery = "select m from Member m join fetch m.team";
                // 한번에 가져오기 때문에 프록시 객체가 아니다!
                List<Member> fetchJoinResult = em.createQuery(fetchQuery, Member.class).getResultList();
                for (Member member : fetchJoinResult) {
                    System.out.println("username = " + member.getUsername() + ", " + "teamName = " + member.getTeam().getName());
                }

                em.clear();
                // distinct 해도 아무 소용 없음 -> join한 member의 정보가 다르기 때문
                // 그런데, fetch join을 사용한 경우, 동일한 엔티티에 대해서의 중복 제거(애플리케이션 차원)
                String fetchQueryForCollections = "select distinct t from Team t join fetch t.members where t.name = 'teamA'";
                List<Team> fetchQueryForCollectionsResult = em.createQuery(fetchQueryForCollections, Team.class).getResultList();
                for (Team team : fetchQueryForCollectionsResult) {
                    System.out.print("team: " + team.getName() + " has member: " + team.getMembers().size());
                    for (Member member : team.getMembers()) {
                        System.out.println(" -> member = " + member);
                    }
                }

                /**
                 * join과 fetch join의 차이는 join의 경우 쿼리문에서 join을 사용하나 영속성 컨텍스트로 데이터를 퍼올리지 않는다...
                 * 따라서 N+1 문제 여전히 존재
                 *
                 * 패치 조인은 즉시 로딩이며, 객체 그래프를 SQL 한번에 조회하는 개념!!
                 */

                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                tx.rollback();
            } finally {
                if (em!=null) {
                    em.close();
                }
            }
        }
    }
}
