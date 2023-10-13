package kitchenpos.application;

import static kitchenpos.fixture.TableFixture.EMPTY_TABLE;
import static kitchenpos.fixture.TableFixture.createTableByEmpty;
import static kitchenpos.fixture.TableFixture.createTableById;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.dao.TableGroupDao;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.TableGroup;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
class TableServiceTest {

    @Autowired
    private TableService tableService;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private TableGroupDao tableGroupDao;

    @Autowired
    private OrderTableDao orderTableDao;

    @Test
    void create_메서드는_주문_테이블을_생성한다() {
        // when
        final OrderTable createdOrderTable = tableService.create(EMPTY_TABLE);

        // then
        assertThat(createdOrderTable)
                .usingRecursiveComparison()
                .isEqualTo(createTableById(createdOrderTable.getId()));
    }

    @Test
    void list_메서드는_모든_주문_테이블을_조회한다() {
        // given
        final OrderTable orderTable = orderTableDao.save(EMPTY_TABLE);

        // when
        final List<OrderTable> tables = tableService.list();

        // then
        assertThat(tables)
                .usingRecursiveComparison()
                .isEqualTo(List.of(orderTable));
    }

    @Nested
    class changeEmpty_메서드는 {

        @Test
        void 주문_테이블의_주문_가능_상태를_바꾼다() {
            // given
            final OrderTable createdOrderTable = tableService.create(createTableByEmpty(true));

            // when
            createdOrderTable.setEmpty(false);
            final OrderTable changedOrderTable = tableService.changeEmpty(createdOrderTable.getId(), createdOrderTable);

            // then
            assertThat(changedOrderTable.isEmpty()).isFalse();
        }

        @Test
        void 주문_테이블이_존재하지_않으면_예외가_발생한다() {
            // given
            long nonExistTableId = 99L;
            final OrderTable orderTable = createTableById(nonExistTableId);

            // when & then
            assertThatThrownBy(() -> tableService.changeEmpty(nonExistTableId, orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 주문_테이블이_그룹에_속해있으면_예외가_발생한다() {
            // given
            final OrderTable orderTable = orderTableDao.save(EMPTY_TABLE);
            final TableGroup tableGroup = new TableGroup(LocalDateTime.now(), List.of(orderTable));
            final TableGroup createdTableGroup = tableGroupDao.save(tableGroup);

            orderTable.setTableGroupId(createdTableGroup.getId());
            orderTableDao.save(orderTable);

            // when & then
            assertThatThrownBy(() -> tableService.changeEmpty(orderTable.getId(), orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 주문_테이블의_주문_상태가_요리중이라면_예외가_발생한다() {
            // given
            final OrderTable orderTable = orderTableDao.save(EMPTY_TABLE);
            final Order order = new Order(orderTable.getId(), OrderStatus.COOKING.name(), LocalDateTime.now(),
                    List.of());
            orderDao.save(order);

            // when & then
            assertThatThrownBy(() -> tableService.changeEmpty(orderTable.getId(), orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 주문_테이블의_주문_상태가_식사중이라면_예외가_발생한다() {
            // given
            final OrderTable orderTable = orderTableDao.save(EMPTY_TABLE);
            final Order order = new Order(orderTable.getId(), OrderStatus.MEAL.name(), LocalDateTime.now(), List.of());
            orderDao.save(order);

            // when & then
            assertThatThrownBy(() -> tableService.changeEmpty(1L, orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class changeNumberOfGuests_메서드는 {

        @Test
        void 주문_테이블의_손님수를_변경한다() {
            // given
            final OrderTable orderTable = orderTableDao.save(createTableById(null));

            // when
            orderTable.setNumberOfGuests(10);
            final OrderTable changedOrderTable = tableService.changeNumberOfGuests(1L, orderTable);

            // then
            assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(10);
        }

        @Test
        void 주문_테이블이_존재하지_않으면_예외가_발생한다() {
            // given
            final OrderTable orderTable = createTableById(null);

            // when & then
            assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void 주문_테이블이_주문_불가능한_상태이면_예외가_발생한다() {
            // given
            final OrderTable orderTable = orderTableDao.save(createTableByEmpty(true));

            // when & then
            assertThatThrownBy(() -> tableService.changeNumberOfGuests(1L, orderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
