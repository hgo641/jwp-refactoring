package kitchenpos.application;

import static kitchenpos.fixture.MenuGroupFixture.CHICKEN_SET;
import static kitchenpos.fixture.MenuGroupFixture.CHICKEN_SET_NON_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.domain.MenuGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceTest
class MenuGroupServiceTest {

    @Autowired
    private MenuGroupDao menuGroupDao;

    @Autowired
    private MenuGroupService menuGroupService;

    @Test
    void create_메서드는_메뉴_그룹을_생성한다() {
        // when
        final MenuGroup response = menuGroupService.create(CHICKEN_SET_NON_ID);

        // then
        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(CHICKEN_SET);
    }

    @Test
    void list_메서드는_모든_메뉴_그룹을_조회한다() {
        // given
        menuGroupDao.save(CHICKEN_SET_NON_ID);

        // when
        final List<MenuGroup> menuGroups = menuGroupService.list();

        // then
        assertThat(menuGroups)
                .usingRecursiveComparison()
                .isEqualTo(List.of(CHICKEN_SET));
    }
}
