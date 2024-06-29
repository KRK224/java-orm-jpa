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

            // 비영속
//            Member member = new Member();
//            member.setId(101L);
//            member.setName("Hello101");
//
//            System.out.println("===Before===");
            // 영속
//            em.persist(member);
//            System.out.println("===After===");

            Member member101 = em.find(Member.class, 101L);
            Member member101_sec = em.find(Member.class, 101L);

            System.out.println("member101.id = " + member101.getId());
            System.out.println("member101.getName() = " + member101.getName());
            System.out.println("member101 == member101_sec ? " + (member101==member101_sec) );


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
