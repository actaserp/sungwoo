package mes.domain.entity.actasEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
@Getter
@Entity
@Setter
@Table(name = "TB_DA006W") //WEB주문서HEAD정보 table
@NoArgsConstructor
public class TB_DA006W {

    @EmbeddedId
    private TB_DA006W_PK id;

    @Column(name = "\"cltcd\"")  // 거래처코드
    String cltcd;

    @Column(name = "\"cltnm\"")  // 용도별
    String cltnm;

    @Column(name = "\"saupnum\"")  // 용도별
    String saupnum;

    @Column(name = "\"cltzipcd\"")  // 용도별
    String cltzipcd;

    @Column(name = "\"cltaddr\"")  // 용도별
    String cltaddr;

    @Column(name = "\"delzipcd\"")  // 용도별
    String delzipcd;

    @Column(name = "\"deladdr\"")  // 용도별
    String deladdr;

    @Column(name = "\"deldate\"")  // 용도별
    String deldate;

    @Column(name = "\"perid\"")  // 용도별
    String perid;

    @Column(name = "\"divicd\"")  // 용도별
    String divicd;

    @Column(name = "\"domcls\"")  // 용도별
    String domcls;

    @Column(name = "\"moncls\"")  // 용도별
    String moncls;

    @Column(name = "\"monrate\"")  // 용도별
    String monrate;

    @Column(name = "\"remark\"")  // 용도별
    String remark;

    @Column(name = "\"operid\"")  // 용도별
    String operid;

    @Column(name = "\"dperid\"")  // 용도별
    String dperid;

    @Column(name = "\"sperid\"")  // 용도별
    String sperid;

    @Column(name = "\"ordflag\"")  // 용도별
    String ordflag;

    @Column(name = "\"egrb\"")  // 용도별
    String egrb;

    @Column(name = "\"fgrb\"")  // 용도별
    String fgrb;

    @Column(name = "\"panel_ht\"")  // 용도별
    String panel_ht;

    @Column(name = "\"panel_hw\"")  // 용도별
    String panel_hw;

    @Column(name = "\"panel_hl\"")  // 용도별
    String panel_hl;

    @Column(name = "\"panel_hh\"")  // 용도별
    String panel_hh;

    @Column(name = "\"indate\"")  // 용도별
    String indate;

    @Column(name = "\"inperid\"")  // 용도별
    String inperid;

    @Column(name = "\"tel\"")  // 용도별
    String tel;

}
