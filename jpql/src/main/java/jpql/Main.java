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

public class Main {
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

            System.out.println("============== 쿼리 시작 ==========");
            // 이때 자동으로 flush 호출
            TypedQuery<Member> query1 = em.createQuery("select m from Member m", Member.class);
            List<Member> resultList = query1.getResultList();

            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

            System.out.println("============ getSingleResult test ===========");
            TypedQuery<Member> singleQuery = em.createQuery("select m from Member m where m.id = 10", Member.class);
            // 오직 결과가 하나인 경우만 사용, 그 외에 값이 없거나 많으면 exception 터진다... -> Spring Data JPA에서는 추상화를 통해 try catch 해서 return null or Optional 반환해준다.
            try {
                Member singleResult = singleQuery.getSingleResult();
                System.out.println("singleResult = " + singleResult);
            } catch (NoResultException nre) {
                System.out.println("There is no result");
            } catch(NonUniqueResultException nue) {
                System.out.println("There are many result");
            }

            System.out.println("============== parameter query test ============");
            Member paramQueryResult = em.createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1").getSingleResult();

            System.out.println("paramQueryResult = " + paramQueryResult);


            // 이렇게 반환 타입이 명확한 경우는 TypedQuery와 Generic으로 반환된다.
            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);
            // 반환 타입이 명확하지 않은 경우는 Query.
            Query query3 = em.createQuery("select m.username, m.age from Member m");



        } catch(Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.clear();
        }

        emf.close();
    }
}