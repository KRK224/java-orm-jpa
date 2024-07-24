package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;

public class JpqlCaseMain {
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
                member.setUsername("관리자");
                member.setAge(10);
                member.setType(MemberType.ADMIN);

                Member user1 = new Member();
                user1.setUsername("user1");
                user1.setAge(35);
                user1.setType(MemberType.USER);

                user1.changeTeam(team);
                em.persist(user1);
                member.changeTeam(team);
                em.persist(member);

                em.flush();
                em.clear();

                /**
                 * sql 조건식 case 문
                 */
                String caseQuery = "select case " +
                        "when m.age <=10 then '학생요금' " +
                        "when m.age >=60 then '경로요금' " +
                        "else '일반요금' " +
                        "end " +
                        "from Member m";

                List<String> caseQueryResult = em.createQuery(caseQuery, String.class).getResultList();
                for (String s : caseQueryResult) {
                    System.out.println("요금 제도 = " + s);
                }

                String coalesceQuery = "select coalesce(m.username, '이름 없는 회원') from Member m";
                List<String> coalesceQueryResult = em.createQuery(coalesceQuery, String.class).getResultList();
                for (String s : coalesceQueryResult) {
                    System.out.println("회원 이름 = " + s);
                }


                String nullIfQuery = "select NULLIF(m.username, '관리자') from Member m";
                List<String> resultList = em.createQuery(nullIfQuery, String.class).getResultList();
                for (String userName : resultList) {
                    System.out.println("userName = " + userName);
                }

                String concatQuery = "select 'a' || 'b' from Member m";
                String concatQuery1 = "select concat('a', 'b') from Member m";

                String locateQuery = "select locate('cd', 'abcdef') from Member m"; // abcdef에서 cd의 위치가 반환
                String sizeQuery = "select Size(t.members) from Team t"; // collection의 사이즈가 반환된다.

                List<Integer> resultList1 = em.createQuery(sizeQuery, Integer.class).getResultList();
                
                for (Integer i : resultList1) {
                    System.out.println("i = " + i);
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
