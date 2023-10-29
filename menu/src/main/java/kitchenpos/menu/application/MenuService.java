package kitchenpos.menu.application;

import java.util.List;
import java.util.stream.Collectors;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuProduct;
import kitchenpos.menu.domain.MenuValidator;
import kitchenpos.menu.domain.MenuRepository;
import kitchenpos.menu.dto.MenuRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MenuService {
    private final MenuRepository menuRepository;
    private final MenuValidator menuValidator;

    public MenuService(final MenuRepository menuRepository, final MenuValidator menuValidator) {
        this.menuRepository = menuRepository;
        this.menuValidator = menuValidator;
    }

    @Transactional
    public Menu create(final MenuRequest request) {
        final List<MenuProduct> menuProducts = createMenuProductsByMenuRequest(request);
        final Menu menu = Menu.of(request.getName(), request.getPrice(), request.getMenuGroupId(),
                menuProducts, menuValidator);
        return menuRepository.save(menu);
    }

    private List<MenuProduct> createMenuProductsByMenuRequest(final MenuRequest request) {
        return request.getMenuProducts().stream()
                .map(menuProductRequest -> new MenuProduct(menuProductRequest.getProductId(),
                        menuProductRequest.getQuantity()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Menu> list() {
        return menuRepository.findAll();
    }
}
