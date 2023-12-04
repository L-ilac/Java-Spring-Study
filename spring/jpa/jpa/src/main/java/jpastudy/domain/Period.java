package jpastudy.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Period {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

}
