package hello.itemservice.domain.item;

import lombok.Data;


@Data // ! 핵심 도메인 모델에는 주의해서 사용할 필요가 있다.
// @Getter
// @Setter
public class Item {
    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;
   
    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }

     
}
