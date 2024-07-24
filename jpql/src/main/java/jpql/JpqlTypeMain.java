package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;

public class JpqlTypeMain {
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
                member.setType(MemberType.ADMIN);

                member.changeTeam(team);
                em.persist(member);

                em.flush();
                em.clear();

                // String으로 비교할 때는 package.class로 하드코딩 필요
                String query = "select m.username, 'Hello', true, FALSE from Member m " +
                        "where m.type = jpql.MemberType.ADMIN";
                String paramQuery = "select m.username, 'Hello', true, FALSE from Member m " +
                        "where m.type = :type";

                // List<Object[]> result = em.createQuery(query, Object[].class).getResultList();

                // 이렇게 사용하면 package명 전체를 안써줘도 된다.
                List<Object[]> result = em.createQuery(paramQuery, Object[].class)
                        .setParameter("type", MemberType.ADMIN)
                        .getResultList();


                for (Object[] objects : result) {
                    System.out.println("objects[0] = " + objects[0]);
                    System.out.println("objects[1] = " + objects[1]);
                    System.out.println("objects[2] = " + objects[2]);
                }

                tx.commit();
            } catch (Exception e) {
                if (em!=null && em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } finally {
                if (em!=null) {
                    em.close();
                }
            }

        }
    }
}
