package hellojpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@SequenceGenerator(
        name="PARENT_SEQ_GENERATOR",
        sequenceName = "PARENT_SEQ",
        initialValue = 1, allocationSize = 50
)
@Getter
@Setter
public class Parent {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PARENT_SEQ_GENERATOR")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> children = new ArrayList<>();

    public void addChild(Child child) {
        children.add(child);
        child.setParent(this);
    }
}
