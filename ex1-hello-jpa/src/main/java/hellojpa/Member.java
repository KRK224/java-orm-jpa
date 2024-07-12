package hellojpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table()
@Getter
@Setter
//@TableGenerator(
//        name="MEMBER_SEQ_GENERATOR",
//        table="MY_SEQUENCES",
//        pkColumnValue = "MEMBER_SEQ", allocationSize=1
//)
@SequenceGenerator(
        name="MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ",
        initialValue = 1, allocationSize = 50
)
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator= "MEMBER_SEQ_GENERATOR")
    private Long id;
    @Column(name = "name", nullable = false)
    private String username;

    /**
     * 일대다 양방향: 외래키를 매핑은 해주지만 인서트, 업데이트를 false로 설정 => 읽기 전용으로 설정
     */
    @ManyToOne
    @JoinColumn(name="TEAM_ID", updatable = false, insertable = false)
    private Team team;

    @OneToOne
    @JoinColumn(name="LOCKER_ID")
    private Locker locker;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    @Lob
    private String description; // Lob인데 변수 타입이 String이기 때문에 CLOB으로 생성

    @Transient
    private int temp;


    public Member() {}

}
