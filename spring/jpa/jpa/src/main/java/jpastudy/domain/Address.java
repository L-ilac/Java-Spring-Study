package jpastudy.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter(value = AccessLevel.PRIVATE)
public class Address {
    private String city;
    private String street;
    private String zipcode;
}
