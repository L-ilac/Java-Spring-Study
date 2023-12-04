package jpabook.jpashop.api.restcontroller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/** 
 * * xToOne(OneToOne, ManyToOne) 관계 (컬렉션으로 참조 되지않는 관계)
 * * Order
 * * Order -> Member
 * * Order-> Delivery
*/
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {
    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // ! Hibernate5 모듈을 추가한 상태에서 원하는 엔티티만 LAZY 로딩을 초기화시켜 갖고오려면 아래와 같이 하면 된다.
        for (Order order : all) {
//            order.getMember().getName(); // Member 엔티티 LAZY 강제 초기화
//            order.getDelivery().getAddress(); // Delivery 엔티티 LAZY 강제 초기화
        }

        return all;

        // ! 이 함수 실행시 무한루프 발생 (결과적으로 stackoverflow로 이어짐)
        // * 양방향 연관관계를 가지는 엔티티를 엔티티 그대로 반환하면, 연관관계에 있는 두 객체를 번갈아가면서 무한히 계속 출력한다.
        // * 이를 막으려면, 연관관계의 반대편에 있는 엔티티의 필드에 @JsonIgnore를 통해 json 라이브러리가 출력하지 않게 해야한다.
        // ! 위의 무한루프를 막아도, fetch type이 LAZY로 설정된 엔티티가 오류를 일으킨다.
        // * LAZY로 설정한 필드에는 진짜 엔티티 객체가 아닌 ByteBuddyInterceptor 클래스 객체가 들어가 있기 떄문에, Jackson 라이브러리가 이를 json 형태로 변환하여 출력하려고 하면 오류가 발생한다.
        // * (잭슨 라이브러리가 프록시 객체를 json으로 어떻게 생성해야하는지 모름)
        // * 이를 막으려면 잭슨라이브러리가 프록시 객체를 어떻게 처리하는지를 구현한 hibernate5 모듈을 스프링 빈으로 추가하면된다.
        // * hibernate5 모듈은 초기화된 프록시 객체는 엔티티의 데이터를 json 형태로 변환하고, 초기화되지 않은 프록시 객체는 'null'로 출력한다.
        // ! 이렇게 하지말고, 그냥 dto로 변환해서 api 스펙에 맞게 반환하는게 맞다.
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        List<SimpleOrderDto> result = all.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        // ! 원래는 result를 또다른 result로 감싸서 반환하는게 좋음(확장성 측면). 예시라서 그렇게 안한 것
        // * MemberApiController에 예시 있음

        return result;
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName(); // Member Lazy 초기화
            this.orderDate = order.getOrderDate();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress(); // Delivery Lazy 초기화
        }

    }

    // ! V1과 V2 모두 LAZY 로딩으로 인해, 데이터베이스에 쿼리가 너무 많이 요청되는 문제가 남아있다.(N+1 problem)
    // ! 1(최초에 특정 엔티티를 N개를 조회하는 쿼리) + [N(조회한 엔티티의 갯수) * 연관된 또다른 엔티티의 갯수](조회한 엔티티에 연관된 또 다른 엔티티를 조회하는 쿼리)
    // ! N+1 문제는 로딩방식을 EAGER로 변경해도 해결되지 않는다.
    // * 따라서 LAZY로 설정한 상태로 페치조인을 통해 최적화를 해야한다.

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> all = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = all.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();

    }
}
