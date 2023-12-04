package jpastudy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jpastudy.RoleType;
import lombok.Getter;
import lombok.Setter;

// @Entity
@SequenceGenerator(name = "member_seq_gen", sequenceName = "mem_seq", initialValue = 1)
@Getter
@Setter
public class Member1 {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_seq_gen") // id를 직접 할당하는 경우에는 @Id만 사용하고, db에서 자동으로 생성하여 할당하려면 둘다 사용
    private Long id;

    @Column(name = "name") // db 상에서 사용될 column 이름 지정
    private String username;

    private Integer age;

    @Enumerated(EnumType.STRING) // 객체에서 enum 타입을 사용하고 싶은 경우
    private RoleType roleType;

    @Temporal(TemporalType.TIMESTAMP) // 날짜+시간 타입을 사용할 때 (option이 총 3가지임)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

    // ! 위의 @Temporal 대신 LocalDate, LocalDateTime를 사용하면 된다.
    private LocalDate testLocalDate; // -> date
    private LocalDateTime testLocalDateTime; // -> timestamp

    @Lob // 대형 데이터와 관련된 필드로 타입이 String라면 clob, 나머지는 blob으로 매핑된다.
    private String description;

    @Transient // db에 저장될 필요 없는 필드, 매핑이 되지 않는다.
    private int tmp;

    public Member1() {
    }

}
