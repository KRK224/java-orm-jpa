package hellojpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(
        name="TEAM_SEQ_GENERATOR",
        sequenceName = "TEAM_SEQ",
        allocationSize = 50, initialValue = 1
)
public class Team extends BaseEntity {

    @Id @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="TEAM_SEQ_GENERATOR")
    @Column(name = "TEAM_ID")
    private Long id;
    private String name;

    @OneToMany()
    @JoinColumn(name="TEAM_ID") // 일대다 관계. 테이블에서는 항상 다가 외래키를 가지고 있다. 그런데 여기서 JoinColumn을 써서 연관관계 주인으로 만듬.
    private List<Member> members = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
