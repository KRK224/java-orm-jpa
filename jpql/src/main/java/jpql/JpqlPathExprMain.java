package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.Collection;
import java.util.List;

public class JpqlPathExprMain {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql")) {
            EntityManager em = null;
            EntityTransaction tx = null;
            try {
                em = emf.createEntityManager();
                tx = em.getTransaction();
                tx.begin();

                Team team1 = new Team();
                team1.setName("team1");
                em.persist(team1);

                Member member1 = new Member();
                member1.setUsername("관리자1");
                em.persist(member1);

                member1.changeTeam(team1);

                Member member2 = new Member();
                member2.setUsername("관리자2");
                em.persist(member2);

                member2.changeTeam(team1);

                em.flush();
                em.clear();

                // 단일 값 연관 경로: 묵시적 내부 조인(inner join) 발생.
                String query = "select m.team from Member m";

//                List<Member> resultList = em.createQuery(query, Member.class).getResultList();
//                for (Member member : resultList) {
//                    // ClassCastException
//                    System.out.println("member = " + member);
//                }

                List<Team> resultList1 = em.createQuery(query, Team.class).getResultList();
                for (Team team : resultList1) {
                    System.out.println("team = " + team);
                }

                String collectionQuery = "select t.members from Team t";
                List<Collection> resultList = em.createQuery(collectionQuery, Collection.class).getResultList();
                System.out.println("resultList = " + resultList);

                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                tx.rollback();
            } finally {
                if (em!=null)
                    em.close();
            }

        }
    }
}
