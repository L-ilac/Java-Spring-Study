package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;

@Embeddable // ! 값 타입은 변경이 불가능하게 설계해야한다.
@Getter
public class Address {

    private String city;

    private String street;

    private String zipcode;

    protected Address() {
    }

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }

}
