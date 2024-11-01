package mes.domain.entity.actasEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TB_DA006W_PK implements Serializable {

    private String custcd;      // 회사코드
    private String spjangcd;    // 사업장코드
    private String reqdate;     // 주문일자
    private String reqnum;      // 주문번호
}
