package hellojpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table()
public class Member {
    @Id
    private Long id;
    @Column(name = "name", nullable = false)
    private String username;
    private Integer age;

    @Column(precision = 20, scale=10)
    private BigDecimal salary;

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

    private LocalDate testLocalDate;
    private LocalDateTime testLocalDateTime;

    public Member() {}

}
