package hellojpa;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("DVD")  // default is Entity
@Getter
@Setter
public class Movie extends Item {
    private String director;
    private String actor;
}
