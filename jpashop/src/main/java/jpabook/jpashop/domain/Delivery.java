package jpabook.jpashop.domain;

import static jakarta.persistence.FetchType.*;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy ="delivery", fetch = LAZY)
    private Order order;

    private String city;
    private String street;
    private String zipcode;

    private DeliveryStatus status;


}
