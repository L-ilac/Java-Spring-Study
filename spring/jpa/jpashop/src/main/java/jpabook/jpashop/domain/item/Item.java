package jpabook.jpashop.domain.item;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

// @BatchSize(size = 100) // ! xToOne 관계에서 One에 해당하는 엔티티는 클래스 레벨에 적어줘야함. 하지만 xToOne 관계는 그냥 페치조인으로 가져오는게 낫다.
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Getter
@Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;

    private int price;

    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    // 비즈니스 로직
    // ! 도메인 주도 설계에서 엔티티 자체적으로 해결할 수 있는 기능은 엔티티 안에 비즈니스로직을 넣는 것이 좋다.
    // * 객체의 필드의 값을 변경할 때, 가급적이면 setter를 사용하지않고, 핵심 비즈니스 메서드를 새로 만들어서 사용하는게 좋다.

    // * 재고 증가
    public void addStock(int quantity) {
        this.stockQuantity += quantity;

    }

    // * 재고 감소
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고가 부족합니다.");
        }

        this.stockQuantity = restStock;
    }

}
