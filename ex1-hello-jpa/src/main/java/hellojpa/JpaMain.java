package hellojpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.Hibernate;

public class JpaMain {

    public static void main(String[] args) {
        // 실행 시점에 emf는 하나만 생성된다.
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        // 하지만 em은 **transaction** 단위로 생성된다.
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {//code

            Team teamA = new Team();
            teamA.setName("teamA");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            em.persist(teamB);

            Member member = new Member();
            member.setUsername("user");
            member.setCreatedBy("Kim");
            member.setTeam(teamA);
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("user2");
            member2.setCreatedBy("Kim");
            member2.setTeam(teamB);

            em.persist(member2);

            em.flush();
            em.clear();

            // JSQL n+1 문제
            List<Member> memberList = em.createQuery("select m from Member m", Member.class).getResultList();

            /**
             * Select * from member;
             * EAGER 타입이기 때문에 Team 객체도 필요
             * Select * from team where team_id = ? // 멤버에 물려있는 데이터가 n개이면 n번 조회
             * 최초의 1 쿼리로 인해 n번의 추가 쿼리가 발생 : n+1 문제
             * 이를 방지하기 위해 fetch join과 entity 그래프 사용
             */

//            boolean loaded = emf.getPersistenceUnitUtil().isLoaded(refMember);
//            System.out.println("loaded = " + loaded); // false
//            Hibernate.initialize(refMember); // Hibernate에만 존재하는 초기화 메서드
////            refMember.getUsername(); // 강제 로딩, 모든 JPA에서 사용하는 방법
//            loaded = emf.getPersistenceUnitUtil().isLoaded(refMember);
//            System.out.println("loaded = " + loaded); // true
//
//            em.clear();
//
//            Member member1 = new Member();
//            member1.setUsername("member1");
//            member1.setCreateDate(LocalDateTime.now());
//            member1.setCreatedBy("Kim");
//
//            em.persist(member1);
//
//            em.flush();
//            em.clear();

//            Member refMember2 = em.getReference(Member.class, member1.getId());
//            System.out.println("refMember2.getClass() = " + refMember2.getClass());   // Proxy
//
//            em.detach(refMember2); // ?? Proxy 객체를 더 이상 관리하지 않는다. LazyInitializationException: could not initialize proxy
//            em.close();
//            em.clear();

            // 실제 DB 쿼리가 발생해서 초기화 진행. => 하지만 더 이상 관리하지 않기 때문에 exception 발생.
//            System.out.println("refMember2.getUsername() = " + refMember2.getUsername());

//            Member member2 = new Member();
//            member2.setUsername("member2");
//            member2.setCreatedBy("Kim");
//
//            em.persist(member2);
//
//            em.flush();
//            em.clear();
//
//            Member m1 = em.find(Member.class, member1.getId());
//            Member m2 = em.find(Member.class, member2.getId());
//            System.out.println("m1 == m2 : " + (m1.getClass()==m2.getClass())); // true;
//            Member reference = em.getReference(Member.class, member2.getId());
//            // 이미 m2.getId에 해당하는 Class가 영속성 컨텍스트에 존재한다. 따라서 Member.class의 인스턴스를 가져온다.
//            System.out.println("m1.getClass() == reference.getClass(): " + (m1.getClass()==reference.getClass())); // true
//
//            // em.clear 해야지 다시 가져온다...
//            em.clear();
//
//            reference = em.getReference(Member.class, member2.getId());
//            System.out.println("m1 == reference: " + (m1.getClass()==reference.getClass())); // false
//
//            logic(m1, reference);
//
//            // em.find
////            Member findMember = em.find(Member.class, member.getId());
////            System.out.println("findMember.getId() = " + findMember.getId());
////            System.out.println("findMember.getUsername() = " + findMember.getUsername());
//
//            // em.getReference
//            Member findMemberReference = em.getReference(Member.class, member1.getId());
//            System.out.println("before findMemberReference.getClass() = " + findMemberReference.getClass());
//            System.out.println("findMemberReference.getId() = " + findMemberReference.getId());
//            // 여기 위까지 DB에 쿼리가 나가지 않는다. Hibernate에서 Proxy객체의 target을 이때 채워준다.
//            System.out.println("findMemberReference.getUsername() = " + findMemberReference.getUsername());
//            System.out.println("findMemberReference.getUsername() = " + findMemberReference.getUsername());
//
//            // 초기화 한다고 해서 Proxy 객체가 기존 Entity로 바뀌지는 않는다.
//            System.out.println("after findMemberReference.getClass() = " + findMemberReference.getClass());
//            // 이것도 마찬가지로 find로 가져와도 Proxy객체를 반환. 영속성 컨텍스트 안에서 같은 PK인 경우 동일함을 보장하기 위해..
//            // find 메소드를 호출하면 DB 조회해서 실제 Entity를 생성하긴 함.
//            Member findMemberReference2 = em.find(Member.class, member1.getId());
//            System.out.println("findMemberReference1 == findMemberReference2: " + (findMemberReference == findMemberReference2));


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

    private static void logic(Object m1, Object reference) {
        if (m1!=null && reference!=null) {
            System.out.println("m1 == m2: " + (m1 instanceof Member));
            System.out.println("m1 == m2: " + (reference instanceof Member));
        }
    }
}
