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

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("UserB");
            member.setTeam(team);

            em.persist(member);

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
