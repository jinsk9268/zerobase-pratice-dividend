package com.zerobase.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 따로 모델 클래스를 만드는 이유는 Entity 클래스는 DB 테이블과 직접적으로 매핑되기 위해 만든 클래스 이므로
 * 엔티티 인스턴스를 서비스 코드 내부에서 데이터를 주고받기 위한 용도로 쓰거나
 * 데이터 로직을 변경하는 로직이 들어가게 되면
 * 엔티티 클래스의 원래 역할을 벗어나게 되므로 자기 역할을 벗어난 역할까지 담당하게 되므로 좋지 못한 신호
 * 코드의 재사용성하고는 관계가 없다
 * 코드는 자기 역할만, 이런 저런 역할을 하게되면 복잡해지고 예상하지 못한 사이드이펙트가 발생한다
 * 유지보수가 어려워진다 장기적으로
 * 이 모델 클래스가 DTO나 VO 같은 개념이다
 * 코드의 재사용은 여러 로직의 동작들을 쪼개서 쪼갠 동작간의 유사성을 가진 부분, 비슷한 역할을 하는 동작들을 찾고
 * 그 역할을 일반화해서 코드를 재사용할 수 있도록 코드의 중복을 없에주는 것이다
 */
@Data // 게터, 세터, 투스트링, 이퀄 => 어노테이션 붙일땐 주의!
@NoArgsConstructor
@AllArgsConstructor
@Builder // 디자인 패턴중에 빌더 패턴을 사용할수 있게
public class Company {
    private String ticker;
    private String name;
}
