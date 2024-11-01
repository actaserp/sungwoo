package mes.domain.entity.actasEntity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@Data
@Embeddable
public class TB_XCLIENTId  implements Serializable {
    private String custcd;
    private String cltcd;
    // 매개변수를 받는 생성자 추가
    public TB_XCLIENTId(String custcd, String cltcd) {
        this.custcd = custcd;
        this.cltcd = cltcd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TB_XCLIENTId that = (TB_XCLIENTId) o;
        return Objects.equals(custcd, that.custcd) &&
                Objects.equals(cltcd, that.cltcd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(custcd, cltcd);
    }
}
