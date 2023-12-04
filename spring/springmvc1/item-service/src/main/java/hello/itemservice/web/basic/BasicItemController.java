package hello.itemservice.web.basic;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {
    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();

        model.addAttribute("items", items);

        return "basic/items";

    }

    @GetMapping("/{itemId}")
    public String detail(@PathVariable Long itemId, Model model) {
        Item findItem = itemRepository.findById(itemId);
        model.addAttribute("item", findItem);

        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    // @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName, @RequestParam int price, @RequestParam int quantity,
            Model model) {

        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);
        // Item item = new Item(itemName, price, quantity);
        itemRepository.save(item);

        model.addAttribute("item", item);

        return "basic/item";
    }

    // @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item, Model model) { // ! Model model 생략가능

        itemRepository.save(item);
        // model.addAttribute("item", item); // ! @ModelAttribute("item") 에 의해서 수행됌 + 함수의 Model 파라미터도 생략가능, model 에 들어가는 attribute의 이름이 "item"이 되는 것
        return "basic/item";
    }

    // @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) { // ! ("item") 생략가능

        // ! @ModelAttribute 가 붙은 객체의 클래스명을 맨앞글자만 소문자로 바꿔서 model의 Attribute로 사용한다.
        itemRepository.save(item);
        // model.addAttribute("item", item); 
        return "basic/item";
    }

    // @PostMapping("/add")
    public String addItemV4(Item item) { // ! @ModelAttribute 생략가능

        // ! V3와 동일하게 객체의 클래스명을 맨앞글자만 소문자로 바꿔서 model의 Attribute로 사용한다.
        itemRepository.save(item);
        // model.addAttribute("item", item); 
        return "basic/item";
    }

    // @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item) {

        itemRepository.save(item);
        // model.addAttribute("item", item); 
        return "redirect:/basic/item/" + item.getId(); // ! PRG 적용 
    }

    @PostMapping("/add")
    public String addItemV6(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);

        //! redirectattribute를 사용하면 url 인코딩도 해줌.
        redirectAttributes.addAttribute("itemId", savedItem.getId()); // 리다이렉트 경로에 pathvariable 처럼 들어감
        redirectAttributes.addAttribute("status", true); // 리다이렉트 경로에 pathvariable 같은 형태로 없기 때문에, 쿼리 파라미터로 들어감

        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);

        return "/basic/editForm";

    }

    @PostMapping("/{itemId}/edit")
    public String edit(@ModelAttribute Item item, @PathVariable Long itemId, RedirectAttributes redirectAttributes) {

        itemRepository.update(itemId, item);

        redirectAttributes.addAttribute("status", true);

        return "redirect:/basic/items/{itemId}"; // ! PathVariable이 여기서도 사용가능

    }

    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 20000, 10));
        itemRepository.save(new Item("itemB", 10000, 30));
    }

}
