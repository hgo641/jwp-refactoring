package kitchenpos.application;

public class ChangeEmptyRequest {

    private boolean empty;

    private ChangeEmptyRequest() {
    }

    public ChangeEmptyRequest(boolean empty) {
        this.empty = empty;
    }

    public boolean isEmpty() {
        return empty;
    }
}
