package jpql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class JpqlMain2 {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpql");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);

            em.persist(member);

            /**
             * Projection
             */

            em.flush();
            em.clear();

            // query를 통해 가져온 엔티티는 영속성 컨텍스트에 관리되는 것을 알 수 있다.
            List<Member> resultList = em.createQuery("select m from Member m", Member.class).getResultList();
            Member member1 = resultList.get(0);
            member1.setAge(20);

            // join 쿼리 발생 -> 명시적으로 join 쿼리임을 표시할 것
//            List<Team> resultList1 = em.createQuery("select m.team from Member m", Team.class).getResultList();
            List<Team> resultList2 = em.createQuery("select t from Member m join m.team t", Team.class).getResultList();

            // 이렇게 스칼라 타입의 값을 여러개 쿼리할 때는 어떻게 가져오나?
            // 1. Query 사용
            List resultScala = em.createQuery("select m.username, m.age from Member m").getResultList();
            for (Object o : resultScala) {
                Object[] oList = (Object[]) o;
                Object username = oList[0];
                Object age = oList[1];
                System.out.println("username = " + username);
                System.out.println("age = " + age);
            }
            // 2. Object[] 자동 타입 캐스팅
            List<Object[]> resultList1 = em.createQuery("select m.username, m.age from Member m", Object[].class).getResultList();
            for (Object[] objects : resultList1) {
                for (Object object : objects) {
                    System.out.println("object = " + object);
                }
            }

            // 3. new 연산자 사용.
            // 단 쿼리문 자체는 문자열이기 때문에 객체의 FQN으로 적어줄 것... => 나중에 QueryDSL 사용하면 보완 가능
            List<MemberDto> resultList3 = em.createQuery("select new jpql.MemberDto(m.username, m.age) from Member m", MemberDto.class).getResultList();
            for (MemberDto memberDto : resultList3) {
                System.out.println("memberDto.getUsername() = " + memberDto.getUsername());
                System.out.println("memberDto.getAge() = " + memberDto.getAge());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
