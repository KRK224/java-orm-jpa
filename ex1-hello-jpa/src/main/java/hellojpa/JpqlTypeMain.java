package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.List;

public class JpqlTypeMain {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello")) {
            EntityManager em = null;
            EntityTransaction tx = null;
            try {
                em = emf.createEntityManager();
                tx = em.getTransaction();
                tx.begin();

                Book book = new Book();
                book.setName("돈의 속성");
                book.setAuthor("김승호");

                em.persist(book);

                // 상속 관계의 Entity 명으로 조회 가능 -> DiscriminationValue 값을 바꿔도 조회가 된다!
                List<Item> resultList = em.createQuery("select i from Item i where type(i) = Book", Item.class).getResultList();

                for (Item item : resultList) {
                    System.out.println("item = " + item);
                }

                em.clear();

                // java의 다운 캐스팅 처럼
                List<Item> resultList1 = em.createQuery("select i from Item i where treat(i as Book).author = '김승호'", Item.class).getResultList();
                for (Item item : resultList1) {
                    System.out.println("item2 = " + item);
                }

                tx.commit();
            } catch (Exception e) {
                e.printStackTrace();
                if(tx!=null && tx.isActive())
                    tx.rollback();
            } finally {
                if(em!=null)
                    em.close();
            }
        }
    }
}
