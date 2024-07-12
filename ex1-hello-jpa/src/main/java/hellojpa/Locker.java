package hellojpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Locker {

    @Id
    @GeneratedValue()
    @Column(name="LOCKER_ID")
    private Long id;

    private String name;

    @OneToOne(mappedBy="locker") // 읽기 전용
    private Member member;

}
