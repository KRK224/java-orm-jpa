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
                // Collection fetch join
                // DB join 결과를 생각해보면 멤버 2명이 각각 하나의 로우로 나오고 이를 JPA가 취합을 하는데 2개에 대한 처리를 하기 때문.
                // SQL 상에서는 distinct 해도 아무 소용 없음 -> join한 member의 정보가 다르기 때문
                // 그런데, JPQL fetch join을 사용한 경우, 동일한 엔티티에 대해서의 중복 제거를 애플리케이션 차원에서 해준다.
                String fetchQueryForCollections = "select distinct t from Team t join fetch t.members where t.name = 'teamA'";
                List<Team> fetchQueryForCollectionsResult = em.createQuery(fetchQueryForCollections, Team.class).getResultList();
                for (Team team : fetchQueryForCollectionsResult) {
                    System.out.print("team: " + team.getName() + " has member: " + team.getMembers().size());
                    for (Member member : team.getMembers()) {
                        System.out.println(" -> member = " + member);
                    }
                }

                em.clear();
                /**
                 * join과 fetch join의 차이는 join의 경우 쿼리문에서 join을 사용하나 영속성 컨텍스트로 데이터를 퍼올리지 않는다...
                 * 따라서 N+1 문제 여전히 존재
                 *
                 * 패치 조인은 즉시 로딩처럼 처리되며, 객체 그래프를 SQL 한번에 조회하는 개념!!
                 */
//                String generalJoinQuery = "select t from Team t join t.members m";
//                List<Team> teamListWithPlainJoin = em.createQuery(generalJoinQuery, Team.class).getResultList();
//                for (Team team : teamListWithPlainJoin) {
//                    // 여기까지는 Member id만 가진 Proxy객체를 가지고 있어 size 나옴?
//                    // => 아니네... 뭔가 collection 이라서 다른 거 같은데... Member 객체 자체를 나중에 쿼리를 통해 가지고 옴
//                    System.out.println("team: " + team.getName() + " has member: " + team.getMembers().size());
//                    for (Member member : team.getMembers()) {
//                        // 영속성 컨텍스트에 퍼올려지지 않아서 쿼리가 나감
//                        System.out.println("  --> member = " + member.getUsername());
//                    }
//                }

                em.clear();

                String fetchQueryForCollections1 = "select t from Team t join fetch t.members";
                // collection fetch join을 pagination 한다?
                // 1대다 관계에서는 DB 데이터가 뻥튀기 된다.. 그런데 pagination으로 자른다는건 데이터 정합성에 어긋남
                // teamA -> memberA
                // ---------------- 여기서 잘린다는 의미 => 따라서 JPA에서는 pagination 쿼리를 무시하고 메모리에서 페이징
                // teamA -> memberB
                // **WARN: HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory**
                // 메모리에서 페이징하기 때문에 쿼리로는 데이터가 많아지면 다 올라온다..

//                List<Team> fetchQueryForCollectionsResultWithPageNation = em.createQuery(fetchQueryForCollections1, Team.class)
//                        .setFirstResult(0)
//                        .setMaxResults(1)
//                        .getResultList();
//                for (Team team : fetchQueryForCollectionsResultWithPageNation) {
//                    System.out.print("team: " + team.getName() + " has member: " + team.getMembers().size());
//                    for (Member member : team.getMembers()) {
//                        System.out.println(" -> member = " + member);
//                    }
//                }

                em.clear();

                /**
                 * 그렇다고 fetch join을 풀자니, N+1 문제가 걸린다.. 어떻게 해야할까?
                 * hibernate.default_batch_fetch_size 옵션을 걸고, lazy loading을 적용하면 된다.
                 * => for문에서 N번 쿼리를 날리는게 아닌 fetch_size만큼 in 절을 날려 쿼리를 최적화한다.
                 * <property name="hibernate.default_batch_fetch_size" value="100"/>
                 */

                String defaultBatchFetchSizeTest = "select t from Team t";
                List<Team> defaultBatchFetchSizeTestResult = em.createQuery(defaultBatchFetchSizeTest, Team.class)
                        .setFirstResult(0)
                        .setMaxResults(3)
                        .getResultList();
                for (Team team : defaultBatchFetchSizeTestResult) {
                    System.out.print("team: " + team.getName() + " has member: " + team.getMembers().size() + "\n");
                    for (Member member : team.getMembers()) {
                        System.out.println(" -> member = " + member);
                    }
                }

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
