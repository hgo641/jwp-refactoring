package kitchenpos.dto;

public class MenuProductRequest {
    private final Long productId;
    private final Long quantity;

    public MenuProductRequest(final Long productId, final Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getQuantity() {
        return quantity;
    }
}
