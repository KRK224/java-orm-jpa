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

            Member member = new Member();
            member.setUsername("userB");
            member.setRoleType(RoleType.ADMIN);
            member.setAge(32);
            System.out.println("===============");
            // strategy가 Identity면 id를 알 수 없기 때문에 바로 호출된다.
            // 반면에, strategy가 sequence인 경우, DB에서 next val 만 호출하여 메모리에 적재한 후 사용.
            em.persist(member);
            System.out.println("member.id() = " + member.getId());
            System.out.println("===============");

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
