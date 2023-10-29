package kitchenpos.order.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class OrderLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private Long menuId;
    private long quantity;

    @Embedded
    private OrderLineItemSpec orderLineItemSpec;

    public OrderLineItem() {
    }

    public OrderLineItem(final Long menuId, final long quantity) {
        this(null, menuId, quantity);
    }

    public OrderLineItem(final Long seq, final Long menuId, final long quantity) {
        this.seq = seq;
        this.menuId = menuId;
        this.quantity = quantity;
    }

    public void createOrderSpec(final OrderLineItemSpec orderLineItemSpec) {
        this.orderLineItemSpec = orderLineItemSpec;
    }

    public Long getMenuId() {
        return menuId;
    }

    public Long getSeq() {
        return seq;
    }

    public long getQuantity() {
        return quantity;
    }
}
