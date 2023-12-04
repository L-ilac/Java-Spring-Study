package jpabook.jpashop.api.restcontroller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** 
 * * xToMany(OneToMany) 관계 (컬렉션으로 참조되는 관계)
 * * Order
 * * Order -> orderItems -> Item
*/

@RestController
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    // ! 엔티티 그대로 반환, N+1 문제 발생
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName(); // LAZY 초기화
            order.getDelivery().getAddress(); // LAZY 초기화

            List<OrderItem> orderItems = order.getOrderItems();

            orderItems.get(0).getItem();
//            orderItems.stream().forEach(o -> o.getItem().getName()); // LAZY 초기화(OrderItem, Item 둘다)

            // ! 궁금증 해소용 테스트 결과
            // ! getItem() 만 호출하면 OrderItem만 초기화, getName()까지 호출해야 Item까지 초기화됌
            // ! 컬렉션에 저장된 엔티티 객체가 여러개일 때, 여러개 중 1개만 초기화해도, 남은 모든 엔티티 객체들까지 전부 초기화된다. -> 그냥 order_id로 해당하는 모든 order_item을 전부 갖고와버림
            // ? why -> JPA에서는 일관성을 지키는게 중요할 것이다. 컬렉션에 있는 데이터중에 하나만 초기화 했다고, 초기화된 하나만 컬렉션에 넣어버리면 데이터 정합성에 위배된다.
            // ? 동일 엔티티 2개를 조회했는데, 두 엔티티의 컬렉션에 다른 데이터가 들어가있으면 안될 것이다.
        }
        return all;
    }

    // ! Dto로 변환하여 반환, N+1 문제 발생
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> result = all.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @Getter
    static class OrderDto {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        // ! dto 안에 직접적인 엔티티가 있으면 안됌!(단순히 dto로 엔티티를 감싸기만 했기때문에 결국 엔티티가 노출됌)
        // * dto에는 엔티티에 대한 의존을 완전히 끊어내야한다. 
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // LAZY 초기화
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress(); // LAZY 초기화
            this.orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());

        }

    }

    @Data
    static class OrderItemDto {

        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            this.itemName = orderItem.getItem().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }

    }

    // ! Dto로 변환하여 반환, N+1 문제 해결, 페이징 불가능
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> all = orderRepository.findAllWithItem();
        List<OrderDto> result = all.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // ! Dto로 변환하여 반환, N+1 문제 해결, 페이징 가능(IN을 이용한 쿼리)
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_paging(
            @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "100") int limit) {
        // * xToOne 관계는 페치조인으로 모두 갖고온다.
        List<Order> all = orderRepository.findAllWithMemberDelivery(offset, limit);

        // * 컬렉션 객체의 처리는 지연로딩 + batch_size를 설정하는것으로 해결 -> 지연로딩으로 설정된 컬렉션을 초기화할때, batchsize를 이용하여 데이터를 한번에 갖고온다.
        List<OrderDto> result = all.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // ! DTO로 바로 조회, N+1 문제 발생, 페이징 가능 -> 단건 조회에서 많이 사용하는 방식(단건 조회에서는 쿼리의 총 갯수가 1 + 1 이기 때문에)
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    // ! DTO로 바로 조회, N+1 문제 해결(IN을 이용한 쿼리) : N+1 -> 1+1로 최적화, 페이징 가능
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }

    // ! DTO로 바로 조회, N+1 문제 해결(IN을 이용한 쿼리) : N+1 -> 1+1 -> 1로 최적화, 페이징 불가능
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flatResult = orderQueryRepository.findAllByDto_flat();

        // * OrderFlatDto -> OrderQueryDto 변환과정 필요
        return flatResult.stream().collect(Collectors.groupingBy(
                o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(),
                        o.getAddress()),
                Collectors
                        .mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(),
                                o.getCount()), Collectors.toList())))
                .entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(),
                        e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(Collectors.toList());

    }
}
