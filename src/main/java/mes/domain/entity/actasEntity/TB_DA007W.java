package mes.domain.entity.actasEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter
@Entity
@Setter
@Table(name = "TB_DA007W") //WEB주문서BODY정보 table
@NoArgsConstructor
public class TB_DA007W {

    @EmbeddedId
    private TB_DA007W_PK pk;

    @Column(name = "\"egrb\"")  // 용도별
    String egrb;

    @Column(name = "\"fgrb\"")  // 톤별
    String fgrb;

    @Column(name = "\"hgrb\"")  // 제품구성
    String hgrb;

    @Column(name = "\"panel_t\"")  // 판넬규격(두께)
    String panel_t;

    @Column(name = "\"panel_w\"")  // 판넬규격(폭)
    String panel_w;

    @Column(name = "\"panel_l\"")  // 판넬규격(길이)
    String panel_l;

    @Column(name = "\"panel_h\"")  // 판넬규격(높이)
    String panel_h;

    @Column(name = "\"qty\"")  // 수량
    int qty;

    @Column(name = "\"exfmtypedv\"")  // 외부마감재
    String exfmtypedv;

    @Column(name = "\"infmtypedv\"")  // 외부보강재
    String infmtypedv;

    @Column(name = "\"stframedv\"")  // 내부보강재
    String stframedv;

    @Column(name = "\"stexplydv\"")  // 내부마감재
    String stexplydv;

    // 질문필요 컬럼들 -----------------------------------

    @Column(name = "\"stinplyyn\"")  //
    String stinplyyn;

    @Column(name = "\"stexinsyn\"")  //
    String stexinsyn;

    @Column(name = "\"stininsyn\"")  //
    String stininsyn;

    @Column(name = "\"indate\"")  //
    String indate;

    @Column(name = "\"inperid\"")  //
    String inperid;

    @Column(name = "\"ordtext\"")  // 옵션 및 요청사항
    String ordtext;

    // 질문필요 컬럼들 -----------------------------------


    @Column(name = "\"remark\"")  //
    String remark;

}
