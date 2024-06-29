package hellojpa;

import jakarta.persistence.*;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {
        // 실행 시점에 emf는 하나만 생성된다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        // 하지만 em은 **transaction** 단위로 생성된다.
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {//code

//            Member member = new Member();
//            member.setId(2L);
//            member.setName("HelloB");
//            em.persist(member);

//            Member findMember = em.find(Member.class, 2L);
//            System.out.println("findMember.getName() = " + findMember.getName());
//            System.out.println("findMember.getId() = " + findMember.getId());

//            findMember.setName("HelloJPA");
            // em.persist(findMember); : 마치 자바 collection 객체를 설계한 것 처럼 참조값이 변형된다... ㄷㄷ

            List<Member> findMembers = em.createQuery("select m from Member as m", Member.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();
            for (Member findMember : findMembers) {
                System.out.println("findMember = " + findMember);
                System.out.println("findMember.getName() = " + findMember.getName());
            }

            Member firstMember = em.find(Member.class, 1L);
            // 과연 하나의 객체로 반환할 것인가? (성공)
            System.out.println("firstMember = " + firstMember);
            System.out.println("firstMember.getName() = " + firstMember.getName());


            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            // em이 내부적으로 데이터베이스 커넥션을 물고 있다.
            em.close();
        }
        emf.close();
    }
}
