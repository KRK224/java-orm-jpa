package hellojpa;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
//@DiscriminatorValue("bb")
public class Book extends Item {
    private String author;
    private String isbn;
}
