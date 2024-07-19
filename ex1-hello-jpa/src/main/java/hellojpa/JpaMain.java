package hellojpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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

            /**
             * 값 타입 컬렉션
             */

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "zipcode"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("족발");
            member.getFavoriteFoods().add("피자");

            member.getAddressHistory().add(new AddressEntity("old1", "street", "zipcode"));
            member.getAddressHistory().add(new AddressEntity("old2", "street", "zipcode"));

            em.persist(member);
            em.flush();
            em.clear();
            System.out.println("=========== Start ===============");
            // 조회: 지연 로딩
            Member findMember = em.find(Member.class, member.getId());
            List<AddressEntity> addressHistory = findMember.getAddressHistory();
            for (AddressEntity address : addressHistory) {
                System.out.println("address = " + address);
            }
            Set<String> favoriteFoods = findMember.getFavoriteFoods();
            for (String favoriteFood : favoriteFoods) {
                System.out.println("favoriteFood = " + favoriteFood);
            }

            // 수정 - 불변 객체
            Address homeAddress = findMember.getHomeAddress();
            // 이때 모든 Address History 테이블의 값이 삭제 되고 다시 넣네 ㅇㅂㅇ?
            findMember.getAddressHistory().add(new AddressEntity(homeAddress));
            // 새로 넣어준다.
            Address newHomeAddress = homeAddress.copyAndNew("newHomeCity", null, null);
            findMember.setHomeAddress(newHomeAddress);

            // 여기서는 하나만 삭제하고 하나만 잘 넣는다
            findMember.getFavoriteFoods().remove("치킨");
            findMember.getFavoriteFoods().add("한식");

            // 근데 여기서는 findMember에 해당하는 모든 임베디드 Address 타입을 지우고 새로 넣는다..?ㅇㅂㅇ
            // 값 타입은 추적이 불가능 하다. PK가 없기 때문! 그래서 변경점만 업데이트 하는 것이 아닌! 다 지우고 다시 생성
            // => 값타입 컬렉션 대신 Entity를 활용하자
            findMember.getAddressHistory().remove(0);
//            findMember.getAddressHistory().add(new Address("newCity1", "street", "zipcode"));
            findMember.getAddressHistory().add(new AddressEntity("newCity1", "street", "zipcode"));

            /**
             * 그럼 언제 값 컬렉션을 사용하나?
             * 값을 추적할 필요가 없는 정말 단순한 경우 => e.g. 사용자 설렉트 박스로 선택한 값들을 저장할 때, 진짜 그 순간의 선택만 다시 저장하면 됨.
             * e.g. position.. 값을 추적하지 않는 경우!!!
             */


            /**
             * 임베디드 타입 코드
             */
//            Address workAddress = new Address("workCity", "workStreet", "workZipcode");
//
//            Member member = new Member();
//            member.setUsername("mem1");
//            member.setHomeAddress(new Address("city", "street", "zipcode"));
//            member.setWorkPeriod(new Period(LocalDateTime.of(2019, 12, 22, 1, 13), LocalDateTime.of(2025, 1, 30, 12, 1)));
//            member.setWorkAddress(workAddress);
//            em.persist(member);
//
//            Member member2 = new Member();
//            member2.setUsername("mem2");
////            member2.setWorkAddress(workAddress); // 같은 임베디드 타입을 갖는다...
//            member2.setWorkAddress(new Address(workAddress.getCity(), workAddress.getStreet(), workAddress.getZipcode())); // 그래서 복사한 참조 객체를 넣어주기!
//            member2.setHomeAddress(new Address("city2", "street2", "zipcode2"));
//
//            em.persist(member2);
//
//            // 만약 같은 임베디드 타입을 참조하는 경우, 하나만 변경해도 두 엔티티에 반영되는 사이드 이펙트 발생.
////            member.getWorkAddress().setCity("workNewCity");
//
//            // 복사해서 임베디드 타입을 넣어주는 걸 까먹으면?
//            // 같은 참조를 넣어주는 것을 자바 언어(컴파일) 상 막을 수 없다 => 불변 객체를 만들자!!!
//            // 만약 변경하고 싶다면 새로 만들어서 넣어주기
//            Address newAddress = workAddress.copyAndNew("newCity", null, null);
//            member.setWorkAddress(newAddress);
//
//            System.out.println("equals() test: " + newAddress.equals(newAddress.copyAndNew(null,null,null)));


//            Child child1 = new Child();
//            child1.setName("child1");
//            Child child2 = new Child();
//            child2.setName("child2");
//
//            Parent parent = new Parent();
//            parent.setName("parent");
//            parent.addChild(child1);
//            parent.addChild(child2);
//
//            // 이때 cascade 설정을 하지 않으면 parent만 persist 되고 child는 persist 되지 않는다.
//            // cascade는 부모가 자식의 "단일 소유자"이면서, 라이프 사이틀을 같이 할 때 사용한다.
//            em.persist(parent);
//
//            em.flush();
//            em.clear();
//
//            Parent findParent = em.find(Parent.class, parent.getId());
//            // orphanRemoval = true 설정 시, 부모에서 자식을 제거하면 자식이 삭제된다.
//            // 마찬가지로 부모가 자식의 "단일 소유자" 인 경우 사용한다.
//            findParent.getChildren().remove(0);
//
//            // cascade = CascadeType.REMOVE 설정 시, 부모가 삭제되면 자식도 삭제된다.
//            // orphanRemoval = true도 같은 효과를 가진다.
//            em.remove(findParent);

//            Team teamA = new Team();
//            teamA.setName("teamA");
//            em.persist(teamA);
//
//            Team teamB = new Team();
//            teamB.setName("teamB");
//            em.persist(teamB);
//
//            Member member = new Member();
//            member.setUsername("user");
//            member.setCreatedBy("Kim");
//            member.setTeam(teamA);
//            em.persist(member);
//
//            Member member2 = new Member();
//            member2.setUsername("user2");
//            member2.setCreatedBy("Kim");
//            member2.setTeam(teamB);
//
//            em.persist(member2);
//
//            em.flush();
//            em.clear();
//
//            // JSQL n+1 문제
//            List<Member> memberList = em.createQuery("select m from Member m", Member.class).getResultList();

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
