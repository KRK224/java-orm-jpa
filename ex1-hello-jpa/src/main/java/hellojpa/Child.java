package hellojpa;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@SequenceGenerator(
        name="CHILD_SEQ_GENERATOR",
        sequenceName = "CHILD_SEQ",
        initialValue = 1, allocationSize = 50
)
public class Child {
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="CHILD_SEQ_GENERATOR")
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private Parent parent;
}
