package jpabook.jpashop.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // * 분기를 모두 나눠서 순수 JPQL로만 작성했을 경우
    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    //* JPA Criteria 를 이용한 동적쿼리 생성
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);

        return query.getResultList();

    }

    // 페치 조인을 통해 N+1 문제를 해결한 쿼리(다대일 관계에 적용)
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class).getResultList();
    }

    // * 페이징 적용 -> 컬렉션은 지연로딩으로 가져오되, batchsize등을 이용해서 지연로딩 성능을 최적화한다.
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    // ! 일(Order)대다(OrderItem) 관계에서 페치조인을 하면, OrderItem(같은 Order에 속하는 서로 다른 OrderItem) 개수만큼의 row가 DB SQL 결과로 제공된다.
    // ! 엔티티(Order)는 orderItems라는 컬렉션에 모든 OrderItem을 전부 담고 있어야한다.
    // ! 따라서 JPA에 의해 Order 엔티티 객체 자체(동일한 영속성 컨텍스트에 의해 관리됌)는 1개만 만들어지지만, 쿼리의 반환값으로 같은 객체가 List에 중복으로 여러개(OrderItem 개수 만큼)가 들어가게된다.
    // ! JPA는 만들어놓은 엔티티 객체를 row 갯수만큼 뻥튀기해서 넘겨줘야할지, 아니면 중복되지 않게 줄여서 줘야할지 정할 수 없다.(DB의 결과로는 뻥튀기된 상태로 왔으니까)
    // * 그래서 distinct 키워드를 사용하면 해결된다. -> 실제 DB로 나가는 sql에도 distinct가 추가되고(하지만 사실상 효과는 없음), JPA 상에서 엔티티의 id값이 같으면 중복을 제거해준다.(엔티티는 id가 같다면 내용물이 전부 같을 것이기 때문에 똑같은 것을 여러개 반환해줄 필요가 없음)
    // * Hibernate6 부터는 distinct 키워드가 컬렉션 조회시 default로 들어간다.

    // 페치 조인을 통해 N+1 문제를 해결한 쿼리(일대다 관계에 적용)
    // ! 일대다 페치조인을 하는 순간 페이징이 불가능하다. -> "db상에" 원하는 쿼리를 날릴수가 없음(데이터가 뻥튀기되서, 내가 원하는 기준으로 페이징을 할 수 없기 때문에) 즉, 일대다의 관계에서 일에 해당하는 데이터를 기준으로 페이징하고 싶은데, 조인쿼리가 나가는 순간 다에 해당하는 데이터를 기준으로 페이징할 수밖에 없음
    public List<Order> findAllWithItem() {
        return em.createQuery("select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class).getResultList();

    }

}
