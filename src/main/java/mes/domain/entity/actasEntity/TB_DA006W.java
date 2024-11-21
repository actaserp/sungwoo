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

    @Column(name = "\"remark\"")
    String remark;

    @Column(name = "\"operid\"")
    String operid;

    @Column(name = "\"dperid\"")
    String dperid;

    @Column(name = "\"sperid\"")
    String sperid;

    @Column(name = "\"ordflag\"")
    String ordflag = "0";

    @Column(name = "\"egrb\"")
    String egrb;

    @Column(name = "\"fgrb\"")
    String fgrb;

    @Column(name = "\"panel_ht\"")
    String panel_ht;

    @Column(name = "\"panel_hw\"")
    String panel_hw;

    @Column(name = "\"panel_hl\"")
    String panel_hl;

    @Column(name = "\"panel_hh\"")
    String panel_hh;

    @Column(name = "\"indate\"")
    String indate;

    @Column(name = "\"inperid\"")
    String inperid;

    @Column(name = "\"telno\"")
    String telno;

    @Column(name = "\"adflag\"")  // 관리자 알림확인 칼럼
    String adflag = "0";

    @Column(name = "\"userflag\"")  // 일반거래처 알림확인 칼럼
    String userflag = "1";
}
