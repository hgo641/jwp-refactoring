package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import kitchenpos.domain.TableGroup;
import kitchenpos.domain.repository.MenuGroupRepository;
import kitchenpos.domain.repository.MenuRepository;
import kitchenpos.domain.repository.OrderRepository;
import kitchenpos.domain.repository.OrderTableRepository;
import kitchenpos.domain.repository.ProductRepository;
import kitchenpos.domain.repository.TableGroupRepository;
import kitchenpos.dto.OrderLineItemRequest;
import kitchenpos.dto.OrderRequest;
import kitchenpos.dto.OrderStatusChangeRequest;
import kitchenpos.exception.InvalidOrderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TableGroupRepository tableGroupRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    private Menu menu;
    private MenuGroup menuGroup;
    private TableGroup tableGroup;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        menuGroup = menuGroupRepository.save(new MenuGroup("메뉴그룹"));
        final Product product = productRepository.save(new Product("치킨", BigDecimal.valueOf(10000)));
        menu = menuRepository.save(new Menu("치킨 세트 메뉴", new BigDecimal(20000), menuGroup.getId(),
                List.of(new MenuProduct(null, product.getId(), 1))));
//        menuProductRepository.save(new MenuProduct(null, product.getId(), 1));
        tableGroup = tableGroupRepository.save(new TableGroup(LocalDateTime.now(), null));
        orderTable = orderTableRepository.save(new OrderTable(tableGroup.getId(), 6, false));
    }

    @Nested
    class create_메서드는 {
        @Test
        void 주문을_생성한다() {
            // given
            final OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), 1L);
            final OrderRequest order = new OrderRequest(
                    orderTable.getId(),
                    List.of(orderLineItemRequest)
            );

            // when
            final Order createdOrder = orderService.create(order);

            // then
            final OrderLineItem orderLineItem = new OrderLineItem(null, menu.getId(), 1);
            final Order expected = new Order(
                    1L,
                    orderTable.getId(),
                    OrderStatus.MEAL.name(),
                    List.of(orderLineItem)
            );

            assertSoftly(
                    softly -> {
                        softly.assertThat(createdOrder)
                                .usingRecursiveComparison()
                                .ignoringFields("orderedTime", "orderLineItems")
                                .isEqualTo(expected);
                        softly.assertThat(createdOrder.getOrderLineItems()).hasSize(1);
                    }
            );
        }

        @Test
        void 주문_항목이_비어있으면_예외가_발생한다() {
            // given
            final OrderRequest orderRequest = new OrderRequest(
                    orderTable.getId(),
                    Collections.emptyList()
            );

            // when & then
            assertThatThrownBy(() -> orderService.create(orderRequest))
                    .isInstanceOf(InvalidOrderException.class);
        }

        @Test
        void 주문_항목에_존재하지_않는_메뉴가_있으면_예외가_발생한다() {
            // given
            final long nonExistMenuId = 99L;
            final OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(nonExistMenuId, 1L);
            final OrderRequest order = new OrderRequest(
                    orderTable.getId(),
                    List.of(orderLineItemRequest)
            );

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(InvalidOrderException.class);
        }

        @Test
        void 주문_항목의_메뉴가_중복되면_예외가_발생한다() {
            // given
            final OrderLineItemRequest orderLineItemRequest1 = new OrderLineItemRequest(menu.getId(), 1L);
            final OrderLineItemRequest orderLineItemRequest2 = new OrderLineItemRequest(menu.getId(), 2L);
            final OrderRequest order = new OrderRequest(
                    orderTable.getId(),
                    List.of(orderLineItemRequest1, orderLineItemRequest2)
            );

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(InvalidOrderException.class);
        }

        @Test
        void 주문_테이블이_존재하지_않으면_예외가_발생한다() {
            // given
            final long nonExistTableId = 99L;
            final OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), 1L);
            final OrderRequest order = new OrderRequest(
                    nonExistTableId,
                    List.of(orderLineItemRequest)
            );

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(InvalidOrderException.class);
        }

        @Test
        void 테이블이_주문_불가능_상태인_경우_예외가_발생한다() {
            // given
            final OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), 1L);
            orderTable.changeEmpty(true);
            orderTableRepository.save(orderTable);
            final OrderRequest order = new OrderRequest(
                    orderTable.getId(),
                    List.of(orderLineItemRequest)
            );

            // when & then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(InvalidOrderException.class);
        }
    }

    @Test
    void list_메서드는_모든_주문을_조회한다() {
        // given
        final OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), 1L);
        final OrderRequest order1 = new OrderRequest(
                orderTable.getId(),
                List.of(orderLineItemRequest)
        );
        final OrderRequest order2 = new OrderRequest(
                orderTable.getId(),
                List.of(orderLineItemRequest)
        );
        final Order createdOrder1 = orderService.create(order1);
        final Order createdOrder2 = orderService.create(order2);

        // when
        final List<Order> orders = orderService.list();

        // then
        System.out.println(LocalDateTime.now().truncatedTo(ChronoUnit.MICROS));
        assertThat(orders)
                .usingRecursiveComparison()
                .isEqualTo(List.of(createdOrder1, createdOrder2));
    }

    @Nested
    class changeOrderStatus_메서드는 {
        @Test
        void 주문_상태를_변경한다() {
            // given
            final OrderLineItemRequest orderLineItemRequest = new OrderLineItemRequest(menu.getId(), 1L);
            final OrderRequest order = new OrderRequest(
                    orderTable.getId(),
                    List.of(orderLineItemRequest)
            );
            final Order createdOrder = orderService.create(order);

            // when
            final OrderStatusChangeRequest orderStatusChangeRequest = new OrderStatusChangeRequest("MEAL");
            final Order actual = orderService.changeOrderStatus(createdOrder.getId(), orderStatusChangeRequest);

            // then
            assertThat(actual.getOrderStatus()).isEqualTo(OrderStatus.MEAL.name());
        }

        @Test
        void 존재하지_않는_주문의_상태를_변경하면_예외가_발생한다() {
            // given
            final long nonExistOrderId = 99L;

            // when & then
            final OrderStatusChangeRequest orderStatusChangeRequest = new OrderStatusChangeRequest("MEAL");
            assertThatThrownBy(() -> orderService.changeOrderStatus(nonExistOrderId, orderStatusChangeRequest))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 계산이_완료된_주문의_상태를_변경하면_예외가_발생한다() {
            // given
            final OrderLineItem orderLineItem = new OrderLineItem(null, menu.getId(), 1);
            final Order order = new Order(
                    orderTable.getId(),
                    OrderStatus.COMPLETION.name(),
                    LocalDateTime.now(),
                    List.of(orderLineItem)
            );
            final Order completedOrder = orderRepository.save(order);

            // when & then
            final OrderStatusChangeRequest orderStatusChangeRequest = new OrderStatusChangeRequest("MEAL");
            assertThatThrownBy(() -> orderService.changeOrderStatus(completedOrder.getId(), orderStatusChangeRequest))
                    .isInstanceOf(InvalidOrderException.class);
        }
    }
}
