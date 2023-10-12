# 키친포스

## 요구 사항

### 상품
* [ ] 상품을 생성한다.
  * [ ] [예외] 가격이 null일 경우
  * [ ] [예외] 가격이 음수일 경우
* [ ] 모든 상품을 조회한다.

### 메뉴
* [ ] 메뉴를 생성한다.
  * [ ] [예외] 가격이 null일 경우
  * [ ] [예외] 가격이 음수일 경우
  * [ ] [예외] 메뉴 그룹을 지정하지 않았을 경우
  * [ ] [예외] 메뉴 가격이 메뉴 상품의 총합보다 클 경우(작을 때는 왜 안함?)
* [ ] 모든 메뉴를 조회한다.

### 주문
* [ ] 주문을 접수한다.
  * [ ] 주문 상태를 `조리`로 변경한다.
  * [ ] [예외] 주문 항목이 존재하지 않을 경우
  * [ ] [예외] 주문한 메뉴가 db에 존재하지 않을 경우
  * [ ] [예외] 주문 항목 리스트에 메뉴가 겹치는 주문 항목들이 있을 경우
  * [ ] [예외] 주문을 한 테이블이 존재하지 않을 경우
  * [ ] [예외] 테이블에서 주문할 수 없는 경우(`empty=true`)
* [ ] 주문의 상태를 변경한다.(`조리`, `식사`, `계산 완료`)
* [ ] 모든 주문을 조회한다.

### 테이블
* [ ] 테이블을 생성한다.
* [ ] 모든 테이블을 조회한다.
* [ ] 테이블의 주문 가능 상태를 변경한다.(`사용불가능[empty=true]`, `사용가능[empty=false]`)
  * [ ] [예외] 테이블의 주문 상태가 `조리`, `식사`일 경우
  * [ ] [예외] 테이블이 그룹에 속해있을 경우
* [ ] 손님 수를 변경한다.
  * [ ] [예외] 손님 수가 음수일 경우
  * [ ] [예외] 테이블이 주문 불가능 상태인 경우(`empty=true`)

### 테이블 그룹 
* [ ] 테이블 그룹을 생성한다.
  * [ ] 속한 테이블들을 주문 가능 상태로 변경한다.(`empty=false`)
  * [ ] [예외] 테이블 수가 2개 미만일 경우
  * [ ] [예외] 사용중인 테이블이 포함되어 있을 경우
  * [ ] [예외] 이미 다른 그룹에 속한 테이블이 포함되어 있을 경우
* [ ] 테이블 그룹을 없앤다.
  * [ ] 그룹에 속한 테이블의 상태를 주문 가능 상태로 변경한다.(`empty=false`)
  * [ ] [예외] 주문 상태가 `조리` 또는 `식사`인 테이블이 포함되어 있을 경우

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 상품 | product | 메뉴를 관리하는 기준이 되는 데이터 |
| 메뉴 그룹 | menu group | 메뉴 묶음, 분류 |
| 메뉴 | menu | 메뉴 그룹에 속하는 실제 주문 가능 단위 |
| 메뉴 상품 | menu product | 메뉴에 속하는 수량이 있는 상품 |
| 금액 | amount | 가격 * 수량 |
| 주문 테이블 | order table | 매장에서 주문이 발생하는 영역 |
| 빈 테이블 | empty table | 주문을 등록할 수 없는 주문 테이블 |
| 주문 | order | 매장에서 발생하는 주문 |
| 주문 상태 | order status | 주문은 조리 ➜ 식사 ➜ 계산 완료 순서로 진행된다. |
| 방문한 손님 수 | number of guests | 필수 사항은 아니며 주문은 0명으로 등록할 수 있다. |
| 단체 지정 | table group | 통합 계산을 위해 개별 주문 테이블을 그룹화하는 기능 |
| 주문 항목 | order line item | 주문에 속하는 수량이 있는 메뉴 |
| 매장 식사 | eat in | 포장하지 않고 매장에서 식사하는 것 |
