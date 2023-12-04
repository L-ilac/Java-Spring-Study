package hello.upload.domain;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.ContextLoaderListener;

import hello.upload.controller.ItemController;
import hello.upload.controller.SpringUploadController;

@Repository
public class ItemRepository {

    private final Map<Long, Item> store = new HashMap<>();
    private long sequence = 0L;

    @Autowired
    private SpringUploadController springUploadController;

    public Item save(Item item) {
        item.setId(++sequence);
        store.put(item.getId(), item);

        springUploadController.getClass();

        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }
}
