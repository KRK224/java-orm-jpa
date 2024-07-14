package hellojpa;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;

/**
 * 결론, 기본적으로 Joined 전략을 사용하고 트레이드 오프를 비교하여 Single_table 전략을 고려하자.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE") // DType 컬럼 설정, Joined strategy 경우 자동으로 생성되지는 않는다.
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "DType")
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@DiscriminatorColumn(name = "DType") // TABLE_PER_CLASS 전략에서는 DiscriminatorColumn 이 있어도 사용되지 않는다.
@Getter
@Setter
public abstract class Item {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private int price;
}
