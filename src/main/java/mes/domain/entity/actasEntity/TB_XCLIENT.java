package mes.domain.entity.actasEntity;
import javax.persistence.*;

import lombok.*;




@Entity
@Table(name="TB_XCLIENT")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//거래처정보
public class TB_XCLIENT {

    @EmbeddedId
    private TB_XCLIENTId id; // 복합 키
  /*  @Id
    @Column(name = "custcd", length = 8, nullable = false)
    private String custcd;  //회사 코드(Tb_xa012 -> custcd)

    @Id
    @Column(name = "cltcd", length = 13, nullable = false)
    private String cltcd;   //거래처코드*/

    @Column(name = "cltnm", length = 80)
    private String cltnm;   //거래처명

    @Column(name = "saupnum", length = 13)
    private String saupnum;     //사업자번호

    @Column(name = "prenm", length = 30)
    private String prenm;       //대표자명

    @Column(name = "rnumchk", length = 1)
    private String rnumchk;     //주민번호여부

    @Column(name = "forenum", length = 20)
    private String forenum;     //주민번호

    @Column(name = "corpperclafi", length = 1)
    private String corpperclafi;    //법인구분

    @Column(name = "prenum", length = 13)
    private String prenum;      //법인번호

    @Column(name = "biztype", length = 6)
    private String biztype;     //업태코드

    @Column(name = "bizitem", length = 6)
    private String bizitem;     //업태종류

    @Column(name = "biztypenm", length = 50)
    private String biztypenm;   //업태명

    @Column(name = "bizitemnm", length = 50)
    private String bizitemnm;   //종목명

    @Column(name = "cltdv", length = 5)
    private String cltdv;       //거래처구분

    @Column(name = "zipcd", length = 6)
    private String zipcd;       //우편번호

    @Column(name = "cltadres", length = 255)
    private String cltadres;    //주소

    @Column(name = "dzipcd", length = 6)
    private String dzipcd;      //DM우편주소

    @Column(name = "dadres", length = 255)
    private String dadres;      //DM 주소

    @Column(name = "telnum", length = 30)
    private String telnum;      //전화번호

    @Column(name = "faxnum", length = 30)
    private String faxnum;      //FAX번호

    @Column(name = "liamt")
    private Float liamt;        //한도 금액

    @Column(name = "perid", length = 10)
    private String perid;       //자사담당자

    @Column(name = "agnernm", length = 30)
    private String agnernm;     //업체담당자명

    @Column(name = "agntel", length = 30)
    private String agntel;      //업체담당자전화번호

    @Column(name = "agnhptel", length = 30)
    private String agnhptel;    //업체담당자 HP번호

    @Column(name = "agnercltnm", length = 40)
    private String agnercltnm;  //지급처명

    @Column(name = "spcd", length = 6)
    private String spcd;        //관리대분류

    @Column(name = "area", length = 6)
    private String area;        //관리중분류

    @Column(name = "team", length = 6)
    private String team;        //관리소분류

    @Column(name = "azipcd", length = 6)
    private String azipcd;      //지급처우편번호

    @Column(name = "aaddr", length = 100)
    private String aaddr;       //지급처주소

    @Column(name = "upriceclafi", length = 1)
    private String upriceclafi;     //도소매구분

    @Column(name = "prtcltnm", length = 80)
    private String prtcltnm;    //인쇄거래처명

    @Column(name = "alclcd1", length = 1)
    private String alclcd1;     //ALCLCD1

    @Column(name = "alclcd2", length = 1)
    private String alclcd2;     //ALCLCD2

    @Column(name = "foreyn", length = 1)
    private String foreyn;      //내외구분

    @Column(name = "relyn", length = 1)
    private String relyn;       //거래중지구분

    @Column(name = "bonddv", length = 1, columnDefinition = "DEFAULT '0'")
    private String bonddv;      //recedv

    @Column(name = "hptelnum", length = 30)
    private String hptelnum;    //핸드폰번호

    @Column(name = "nation", length = 4)
    private String nation;      //국가번호

    @Column(name = "homepage", length = 30)
    private String homepage;    //홈페이지

    @Column(name = "agneremail", length = 30)
    private String agneremail;      //담담자 email

    @Column(name = "agneremail1", length = 30)
    private String agneremail1;     //담당자 email1

    @Column(name = "agnerdivinm", length = 30)
    private String agnerdivinm;     //담당자부서

    @Column(name = "remarks", length = 100)
    private String remarks;     //비고

    @Column(name = "clttype", length = 1)
    private String clttype;     //거래구분

    @Column(name = "bankcd", length = 3)
    private String bankcd;      //은행코드

    @Column(name = "bankno", length = 2)
    private String bankno;      //은행지점코드

    @Column(name = "accnum", length = 20)
    private String accnum;      //계좌번호

    @Column(name = "deposit", length = 50)
    private String deposit;     //예금주

    @Column(name = "accpernm", length = 60)
    private String accpernm;    //ACCPERNM

    @Column(name = "natcode", length = 4)
    private String natcode;     //국가코드

    @Column(name = "setcls", length = 10)
    private String setcls;      //결제 구분

    @Column(name = "taxdv", length = 5)
    private String taxdv;       //계산서발행기준

    @Column(name = "givedate", length = 2)
    private String givedate;    //지급일자

    @Column(name = "setcls1", length = 5)
    private String setcls1;     //결제구분1

    @Column(name = "taxdv1", length = 5)
    private String taxdv1;      //계산서발행기준1

    @Column(name = "givedate1", length = 2)
    private String givedate1;   //지급일자 1

    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;      //적요

    @Column(name = "grade", length = 5)
    private String grade;       //등급

    @Column(name = "lvcode", length = 2)
    private String lvcode;      //LVCODE

    @Column(name = "froff", length = 30)
    private String froff;       //화물지점

    @Column(name = "operid", length = 10)
    private String operid;      //현장담당자

    @Column(name = "opertel", length = 30)
    private String opertel;     //현장담당자전화번호

    @Column(name = "ocltcd", length = 13)
    private String ocltcd;      //구거래처코드

    @Column(name = "pperid", length = 10)
    private String pperid;      //자사매입담당자

    @Column(name = "taxpernm", length = 40)
    private String taxpernm;    //계산담당자

    @Column(name = "taxdivnm", length = 30)
    private String taxdivnm;    //계산서담당부서

    @Column(name = "taxmail", length = 100)
    private String taxmail;     //계산서메일

    @Column(name = "taxtelnum", length = 30)
    private String taxtelnum;   //계산서담당전화번호

    @Column(name = "taxsms", length = 30)
    private String taxsms;      //계산SMS

    @Column(name = "carrier", length = 40)
    private String carrier;     //배송업체

    @Column(name = "payterm", length = 50)
    private String payterm;     //결제기한

    @Column(name = "incoterm", length = 50)
    private String incoterm;    //무역조건

    @Column(name = "nopa", columnDefinition = "TEXT")
    private String nopa;        //출항지

    @Column(name = "cltyul")
    private Float cltyul;       //할인율

    @Column(name = "moncls", length = 5)
    private String moncls;      //화폐단위

    @Column(name = "cltynm", length = 40)
    private String cltynm;      //약명

    @Column(name = "wpass", length = 10)
    private String wpass;       //WPASS

    @Column(name = "taxcltcd", length = 13)
    private String taxcltcd;    //계산서발행거래처

    @Column(name = "nicnm1", length = 50)
    private String nicnm1;      // 가맹점1

    @Column(name = "nicnm2", length = 50)
    private String nicnm2;      //가맹점2

    @Column(name = "nicnm3", length = 50)
    private String nicnm3;      //가맹점3

    @Column(name = "nicnm4", length = 50)
    private String nicnm4;      //가맹점4

    @Column(name = "nicnm5", length = 50)
    private String nicnm5;      //가맹점5

    @Column(name = "nicnm6", length = 50)
    private String nicnm6;      //가맹점6

    @Column(name = "prtdv", length = 1)
    private String prtdv;       //보고서제외여부

    @Column(name = "stdate", length = 8)
    private String stdate;      //거래개시일자

    @Column(name = "endate", length = 8)
    private String endate;      //거래중지일자

    @Column(name = "vendor", length = 5)
    private String vendor;      //납품처

    @Column(name = "officd", length = 5)
    private String officd;      //점포코드

    @Column(name = "taxspcd", length = 20)
    private String taxspcd;     //세무 SP코드

    @Column(name = "taxpncd", length = 20)
    private String taxpncd;     //세무 PN코드

    @Column(name = "indate", length = 8)
    private String indate;      //입력일자

    @Column(name = "inperid", length = 10)
    private String inperid;     //입력자

    @Column(name = "inputdate", length = 8)
    private String inputdate;   //inputdate

    @Column(name = "bmar", length = 1)
    private String bmar;        //bmar

    @Column(name = "allchk", length = 1)
    private String allchk;      //allchk

    @Column(name = "emcltcd", length = 20)
    private String emcltcd;     //emcltcd

    @Column(name = "pnbcltcd", length = 20)
    private String pnbcltcd;    //pnbcltcd

    @Column(name = "swcltcd", length = 20)
    private String swcltcd;     //swcltcd

}
