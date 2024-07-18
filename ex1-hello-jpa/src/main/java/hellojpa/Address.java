package hellojpa;

import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter(AccessLevel.PRIVATE) // 불변 객체 만들기!
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String city;
    private String street;
    private String zipcode;

    // 이런 식으로 embedded value 안에 엔티티가 존재할 수 있다.
//    @OneToOne(mappedBy = "phone", fetch = fetchType.LAZY)
//    private PhoneNumber phoneNumber;

    public Address copyAndNew(String city, String street, String zipcode) {
        String newCity = city == null ? this.getCity() : city;
        String newStreet = street == null ? this.getStreet() : street;
        String newZipcode = zipcode == null ? this.getZipcode() : zipcode;
        return new Address(newCity, newStreet, newZipcode);
    }

    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(city, address.city) && Objects.equals(street, address.street) && Objects.equals(zipcode, address.zipcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, zipcode);
    }
}
