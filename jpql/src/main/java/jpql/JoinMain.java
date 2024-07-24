package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;

public class JoinMain {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql")) {
            EntityManager em = null;
            try {
                em = emf.createEntityManager();
                EntityTransaction tx = em.getTransaction();
                tx.begin();

                Team team = new Team();
                team.setName("teamA");
                em.persist(team);


                Member member = new Member();
                member.setUsername("teamA");
                member.setAge(10);

                member.changeTeam(team);
                em.persist(member);

                em.flush();
                em.clear();

//                String joinQuery = "select m from Member m inner join m.team t";
//                List<Member> resultList = em.createQuery(joinQuery, Member.class).getResultList();
//                for (Member member1 : resultList) {
//                    System.out.println("member1 = " + member1);
//                }

                String thetaJoin = "select m from Member m, Team t where m.username = t.name";
                List<Member> resultList = em.createQuery(thetaJoin, Member.class).getResultList();
                for (Member member1 : resultList) {
                    System.out.println("member1 = " + member1);
                }

                // 조인 대상 필터링
                String query = "select m from Member m left join m.team t ON t.name = 'teamA'";
                List<Member> resultList1 = em.createQuery(query, Member.class).getResultList();
                for (Member member1 : resultList1) {
                    System.out.println("member1 = " + member1);
                }

                // 연관관계 없는 엔티티 외부 조인
                // 연관관계가 있는 쿼리와의 차이점에 유의하자 - m.team t / Team t
                String query1 = "select m from Member m left join Team t on t.name = m.username";
                List<Member> resultList2 = em.createQuery(query1, Member.class).getResultList();
                for (Member member1 : resultList2) {
                    System.out.println("member1 = " + member1);
                }


            } catch (Exception e) {
                e.printStackTrace();
                if (em!=null && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } finally {
                if (em!=null)
                    em.close();
            }
        }
    }
}
