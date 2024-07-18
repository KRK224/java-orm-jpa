package hellojpa;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String city;
    private String street;
    private String zipcode;

    // 이런 식으로 embedded value 안에 엔티티가 존재할 수 있다.
//    @OneToOne(mappedBy = "phone", fetch = fetchType.LAZY)
//    private PhoneNumber phoneNumber;

}
