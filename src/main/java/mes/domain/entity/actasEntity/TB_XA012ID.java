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
public class TB_XA012ID implements Serializable {
    private String custcd;
    private String spjangcd;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TB_XA012ID that = (TB_XA012ID) o;
        return Objects.equals(custcd, that.custcd) &&
                Objects.equals(spjangcd, that.spjangcd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(custcd, spjangcd);
    }
}
