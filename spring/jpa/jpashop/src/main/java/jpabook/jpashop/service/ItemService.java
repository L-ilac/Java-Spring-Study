package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    // @Transactional 
    // public Item updateItem(Long itemId, Book param) {
    //     Item findItem = itemRepository.findOne(itemId);

    //     findItem.setName(param.getName());
    //     findItem.setPrice(param.getPrice());
    //     findItem.setStockQuantity(param.getStockQuantity());

    //     return findItem;
    // }

    // ! em.merge()가 아닌 변경감지를 이용하여 엔티티를 수정하는 함수, 인자로 엔티티를 직접 받아오는 것은 좋은 설계가 아니므로, 위의 함수보다는 아래의 함수처럼 변경하는 것이 좋다.
    // * 변경감지는 트랜잭션 내에서, 영속성 컨텍스트에 의해 관리되는 엔티티에만 적용된다.
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        Item findItem = itemRepository.findOne(itemId);

        // ! setter 사용대신 findItem.change(name, price, stockQuantity) 와 같은 변경을 위한 전용 함수를 사용하자.
        // * 물론 change() 함수는 엔티티 내부에 선언된 함수이기 때문에, this.특정필드 와 같은 방법으로 데이터를 변경할 수 있다.
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantity);
        // findItem.change(name, price, stockQuantity);
    }

    // @Transactional
    // public void updateItem(Long itemId, ItemUpdateDto itemUpdateDto) {
    //     Item findItem = itemRepository.findOne(itemId);

    //     findItem.change(itemUpdateDto);
    // }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }

}
