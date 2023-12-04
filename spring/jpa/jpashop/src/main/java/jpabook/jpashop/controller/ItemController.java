package jpabook.jpashop.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;

import jpabook.jpashop.service.ItemService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());

        return "items/createItemForm";

    }

    @PostMapping("/items/new")
    public String create(@ModelAttribute BookForm form) {

        Book book = new Book();

        book.setName(form.getName());
        book.setIsbn(form.getIsbn());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());

        itemService.saveItem(book);

        return "redirect:/items";

    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);

        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable Long itemId, Model model) {
        // Item item = itemService.findOne(itemId);

        // * 예제를 간단하게 하기 위해 다운캐스팅함
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);

        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@ModelAttribute BookForm form, @PathVariable Long itemId) {

        // Book book = new Book();
        // book.setId(form.getId());
        // book.setName(form.getName());
        // book.setPrice(form.getPrice());
        // book.setStockQuantity(form.getStockQuantity());
        // book.setAuthor(form.getAuthor());
        // book.setIsbn(form.getIsbn());

        // itemService.updateItem(itemId, book);
        // itemService.saveItem(book);

        // ! 웹 계층에서 사용하는 dto를 서비스계층에서 사용할 수 있는 형태로 데이터를 넘겨주려면, 위와 같이 지저분하게 dto를 엔티티로 변환하여 함수 파라미터로 전달해야한다.
        // ! 그렇게 하지말고, 차라리 dto에 들어가있는 데이터중 서비스계층에서 필요한 데이터만 뽑아서, 서비스 계층의 함수로 넘겨주는 방식으로 서비스 계층의 함수를 설계하는게 낫다.
        // * 그래야 훨씬 깔끔하고, 유지보수 측면에서 더 나은 코드이다.

        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        // * 만약 서비스 계층으로 넘겨줘야할데이터가 너무 많다면, 서비스 계층에 dto를 하나 만들어서 전달하는 방법을 사용하는게 좋다.
        // ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        // Bookform -> ItemUpdateDto 로 변환하는 코드
        // itemService.updateItem(itemId, itemUpdateDto);

        return "redirect:/items";
    }
}
