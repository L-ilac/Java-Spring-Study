package hello.itemservice.domain.item;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ItemRepositoryTest {
    ItemRepository itemRepository = new ItemRepository();

    @AfterEach
    void afterEach() {
        itemRepository.clearStore();

    }

    @Test
    void testClearStore() {

    }

    @Test
    void testFindAll() {
        Item item1 = new Item("itemA", 10000, 10);
        itemRepository.save(item1);
        Item item2 = new Item("itemB", 20000, 30);
        itemRepository.save(item2);

        List<Item> items = itemRepository.findAll();
        Assertions.assertThat(items.size()).isEqualTo(2);
        Assertions.assertThat(items).contains(item1, item2);
    }

    @Test
    void testFindById() {

    }

    @Test
    void testSave() {
        Item item = new Item("itemA", 10000, 10);

        Item savedItem = itemRepository.save(item);

        Item findItem = itemRepository.findById(item.getId());

        Assertions.assertThat(savedItem).isEqualTo(findItem);
    }

    @Test
    void testUpdate() {
        Item item1 = new Item("itemA", 10000, 10);
        Item savedItem = itemRepository.save(item1);

        Long itemId = savedItem.getId();

        Item updateParam = new Item("itemB", 20000, 30);
        itemRepository.update(itemId, updateParam);

        Item findItem = itemRepository.findById(itemId);

        Assertions.assertThat(findItem.getPrice()).isEqualTo(updateParam.getPrice());
        Assertions.assertThat(findItem.getQuantity()).isEqualTo(updateParam.getQuantity());
        Assertions.assertThat(findItem.getItemName()).isEqualTo(updateParam.getItemName());

    }
}
