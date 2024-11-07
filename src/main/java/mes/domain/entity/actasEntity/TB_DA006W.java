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
    private TB_DA006W_PK pk;

    @Column(name = "\"cltcd\"")  // 거래처코드
    String cltcd;

    @Column(name = "\"cltnm\"")  // 거래처명
    String cltnm;

    @Column(name = "\"saupnum\"")  // 사업자번호
    String saupnum;

    @Column(name = "\"cltzipcd\"")  // 업체우편번호
    String cltzipcd;

    @Column(name = "\"cltaddr\"")  // 업체주소
    String cltaddr;

    @Column(name = "\"cltaddr02\"") // 업체 상세주소
    String cltaddr02;

    @Column(name = "\"delzipcd\"")  //
    String delzipcd;

    @Column(name = "\"deladdr\"")  // 납품주소
    String deladdr;

    @Column(name = "\"deldate\"")  // 납기희망일
    String deldate;

    @Column(name = "\"perid\"")  // 담당자
    String perid;

    @Column(name = "\"divicd\"")  //
    String divicd;

    @Column(name = "\"domcls\"")  //
    String domcls;

    @Column(name = "\"moncls\"")  //
    String moncls;

    @Column(name = "\"monrate\"")  //
    String monrate;

    @Column(name = "\"remark\"")  // 요청사항 내용
    String remark;

    @Column(name = "\"operid\"")  // 용도별
    String operid;

    @Column(name = "\"dperid\"")  // 용도별
    String dperid;

    @Column(name = "\"sperid\"")  // 용도별
    String sperid;

    @Column(name = "\"ordflag\"")  // 용도별
    String ordflag = "0";

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

    @Column(name = "\"telno\"")  // 용도별
    String telno;

}
