package hello.itemservice.web.validation;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import hello.itemservice.domain.item.Item;

@Component
public class ItemValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Item.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Item item = (Item) target;

        // * 검증로직
        // ! errors에 검증 오류 결과를 보관
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "itemName", "required");

        // if (!StringUtils.hasText(item.getItemName())) {
        //     ! V3의 에러 추가 코드를 내부적으로 대신 수행해준다.
        //     errors.rejectValue("itemName", "required");
        // }

        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000) {

            errors.rejectValue("price", "range", new Object[] { 1000, 1000000 }, null);
        }

        if (item.getQuantity() == null || item.getQuantity() >= 9999) {

            errors.rejectValue("quantity", "max", new Object[] { 9999 }, null);
        }

        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {

                errors.reject("totalPriceMin", new Object[] { 10000, resultPrice },
                        null);
            }
        }

    }

}
