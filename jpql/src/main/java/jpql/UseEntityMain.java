package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.transaction.Transaction;
import java.util.List;

public class UseEntityMain {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql")) {
            EntityManager em = null;
            EntityTransaction tx = null;
            try {
                em = emf.createEntityManager();
                tx = em.getTransaction();
                // transaction 시작!
                tx.begin();
                Team teamA = new Team();
                teamA.setName("teamA");
                em.persist(teamA);

                Member member = new Member();
                member.setUsername("userA");
                member.setTeam(teamA);
                em.persist(member);

                em.flush();
                em.clear();

                // jpql에 엔티티 자체를 넘겨줄 때는 엔티티의 PK 값이 적용된다.
                String query = "select m from Member m where m = :member";
                Member findMember = em.createQuery(query, Member.class).setParameter("member", member).getSingleResult();
                System.out.println("findMember = " + findMember);

                em.clear();

                Team findTeam = em.find(Team.class, teamA.getId());

                // 외래키에 할당할 때도 엔티티의 PK 값이 들어가서 join 쿼리가 잘 동작한다.
                String findMemberByTeam = "select m from Member m where m.team = :team";
                Member findMemberByTeamResult = em.createQuery(findMemberByTeam, Member.class).setParameter("team", findTeam).getSingleResult();
                System.out.println("findMemberByTeamResult = " + findMemberByTeamResult);


                // 반드시 transaction commit 찍기.
                tx.commit();

            } catch (Exception e) {
                e.printStackTrace();
                if (tx!=null && tx.isActive()) {
                    tx.rollback();
                }

            } finally {
                if (em!=null)
                    em.close();
            }

        }


    }
}
