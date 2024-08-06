package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;

public class NamedQueryMain {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql")) {
            EntityManager em = null;
            EntityTransaction tx = null;

            try {
                em = emf.createEntityManager();
                tx = em.getTransaction();
                tx.begin();

                Team team = new Team();
                team.setName("teamA");
                em.persist(team);

                Member member = new Member();
                member.setUsername("memberA");
                member.setTeam(team);
                em.persist(member);

                // NamedQuery annotation에 정의한 이름을 사용
                // 그리고 NamedQuery의 경우 애플리케이션 로딩 시점에 문자열을 파싱하기 때문에 문법 에러를 낼 수 있다.
                // 개발에서 가장 좋은 에러는 컴파일 에러이다 => 생성자 등등을 사용하는 이유.
                /**
                 * Spring DATA JPA에서 DAO 메소드 위에 붙은 @Query 어노테이션이 바로 NamedQuery 이다.
                 * 따라서 동일하게 컴파일 타임에 에러를 발견할 수 있다.
                 */
                List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class).setParameter("username", "memberA").getResultList();
                for (Member findMember : resultList) {
                    System.out.println("findMember = " + findMember);
                }

                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if (tx!=null && tx.isActive())
                    tx.rollback();
            } finally {
                if (em!=null)
                    em.close();
            }
        }
    }
}
