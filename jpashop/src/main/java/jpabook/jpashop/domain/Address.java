package jpabook.jpashop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter(AccessLevel.PRIVATE)
public class Address {
    // 값 타입을 통해 프로젝트 전체에서 사용에 대한 validation을 통일할 수 있다.
    @Column(length = 10)
    private String city;
    @Column(length = 20)
    private String street;
    @Column(length = 5)
    private String zipcode;

    // equals로 필드를 비교할 때, getter를 사용해야 하는 이유
    // Proxy 객체를 사용할 때 필드에 직접 접근할 수 없는 경우가 있다.
    @Override
    public boolean equals(Object o) {
        if (this==o) return true;
        if (o==null || getClass()!=o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(getCity(), address.getCity()) && Objects.equals(getStreet(), address.getStreet()) && Objects.equals(getZipcode(), address.getZipcode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCity(), getStreet(), getZipcode());
    }

    public String getFullAddress() {
        return getCity() + " " + getStreet() + " " + getZipcode();
    }

}
