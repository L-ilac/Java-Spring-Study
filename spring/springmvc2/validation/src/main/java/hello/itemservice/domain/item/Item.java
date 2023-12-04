package hello.itemservice.domain.item;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
// ! 안씀 @ScriptAssert(lang = "javascript", script = "_this.price * _this.quantity >= 10000") 
public class Item {

    // @NotNull(groups = UpdateCheck.class)
    private Long id;

    // @NotBlank(message = "공백 X", groups = { UpdateCheck.class, SaveCheck.class })
    private String itemName;

    // @NotNull(groups = { UpdateCheck.class, SaveCheck.class })
    // @Range(min = 1000, max = 1000000, groups = { UpdateCheck.class, SaveCheck.class })
    private Integer price;

    // @NotNull(groups = { UpdateCheck.class, SaveCheck.class })
    // @Max(value = 9999, groups = { SaveCheck.class })
    private Integer quantity;

    @NotNull()

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
