package jpabook.jpashop.repository.order.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {
        private final EntityManager em;

        private List<OrderQueryDto> findOrders() {
                // ! 여기서 페이징 가능
                return em.createQuery(
                                "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
                                        + " from Order o"
                                        + " join o.member m"
                                        + " join o.delivery d",
                                OrderQueryDto.class)
                        // .setFirstResult(1)
                        // .setMaxResults(100)
                        .getResultList();
        }

        private List<OrderItemQueryDto> findOrderItems(Long orderId) {
                return em.createQuery(
                        "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                                + " from OrderItem oi"
                                + " join oi.item i"
                                + " where oi.order.id = :orderId",
                        OrderItemQueryDto.class).setParameter("orderId", orderId).getResultList();

        }

        public List<OrderQueryDto> findOrderQueryDtos() {

                List<OrderQueryDto> result = findOrders();

                result.forEach(o -> {
                        // ! OrderQueryDto에 들어갈 OrderItemQueryDto를 매번 쿼리를 날려서 조회
                        List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
                        o.setOrderItems(orderItems);
                });

                return result;

        }

        public List<OrderQueryDto> findAllByDto_optimization() {
                List<OrderQueryDto> result = findOrders();

                // ! IN을 이용한 쿼리에 넣어줄 모든 주문의 ID 찾기
                List<Long> orderIds = result.stream()
                                .map(o -> o.getOrderId())
                                .collect(Collectors.toList());

                // ! 모든 OrderQueryDto에 들어갈 모든 OrderItemQueryDto를 한번에 쿼리를 날려서 조회(N개를 1개씩 N번 찾는 쿼리 대신, N개를 한번에 찾는 쿼리)
                List<OrderItemQueryDto> orderItems = em.createQuery(
                                "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                                                + " from OrderItem oi"
                                                + " join oi.item i"
                                                + " where oi.order.id in :orderIds",
                                OrderItemQueryDto.class).setParameter("orderIds", orderIds).getResultList();

                // ! 한번의 쿼리로 모든 OrderItemQueryDto를 다 갖고온 후에, 메모리에서 OrderQueryDto에 넣어주기
                // * key : orderId
                // * value : OrderQueryDto의 orderId에 맞는 List<OrderItemQueryDto>
                Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                                .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));

                result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

                return result;
        }

        public List<OrderFlatDto> findAllByDto_flat() { // ! 페이징 불가 -> 일대다 조인으로 인해서 조회결과가 부풀려졌기 때문(Order 기준으로 페이징 불가)
                List<OrderFlatDto> result = em.createQuery(
                                "select new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)"
                                                + " from Order o"
                                                + " join o.member m"
                                                + " join o.delivery d"
                                                + " join o.orderItems oi"
                                                + " join oi.item i",
                                OrderFlatDto.class).getResultList();

                return result;

        }

}
