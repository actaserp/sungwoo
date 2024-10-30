package mes.domain.entity;


import lombok.Data;
import lombok.NoArgsConstructor;
import mes.domain.DTO.TB_RP940Dto;
import org.apache.commons.math3.analysis.function.Log10;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


import javax.persistence.*;
import java.time.LocalDateTime;


@Table(name = "TB_RP940")     //TODO: 유저권한정보 테이블
@Entity
@Data
@EntityListeners(AuditingEntityListener.class) //해당 클래스에 Auditing 기능을 포함한다.
public class TB_RP940{

    @Column(name = "\"userid\"")
    @Id
    private String userid;

    @Column(name = "\"agencycd\"")
    private String agencycd;

    @Column(name = "\"divinm\"")
    private String divinm;

    @Column(name = "\"ranknm\"")
    private String ranknm;

    @Column(name = "\"usernm\"")
    private String usernm;

    @Column(name = "\"userhp\"")
    private String userhp;



    @Column(name = "\"usermail\"")
    private String usermail;
    @Column(name = "\"loginpw\"")
    private String loginpw;
    @Column(name = "\"authgrpcd\"")
    private String authgrpcd;
    @Column(name = "\"authgrpnm\"")
    private String authgrpnm;


    @Column(name = "\"askreason\"")
    private String askreason;

    @CreatedDate
    @Column(name = "\"askdatem\"", updatable = false)
    private LocalDateTime askdatem;

    @Column(name = "\"appflag\"")
    private String appflag;

    @Column(name = "\"appuser\"")
    private String appuser;


    @Column(name = "\"appdatem\"")
    private OffsetDateTime appdatem;

    @Column(name = "\"appreason\"")
    private String appreason;

    @Column(name = "\"agencynm\"")
    private String agencynm;

    @Column(name = "\"compnm\"")
    private String compnm;
    @Column(name = "\"phone\"")
    private String phone;
    @Column(name = "\"Saupnum\"")
    private String Saupnum;
    @Column(name = "\"Postno\"")
    private String Postno;
    @Column(name = "\"Address1\"")
    private String Address1;
    @Column(name = "\"Address2\"")
    private String Address2;

    public static TB_RP940 toSaveEntity(TB_RP940Dto DTO){
        TB_RP940 tbRp940 = new TB_RP940();
        tbRp940.setUserid(DTO.getId());  //id
        tbRp940.setCompnm(DTO.getCompnm());  // 업체명
        tbRp940.setDivinm(DTO.getAgencyDepartment()); //소속부서
        tbRp940.setRanknm(DTO.getLevel());
        tbRp940.setUsernm(DTO.getName());
        tbRp940.setUserhp(DTO.getTel());
        tbRp940.setPhone(DTO.getPhone());
        tbRp940.setUsermail(DTO.getEmail());
        tbRp940.setLoginpw(DTO.getPassword());
        tbRp940.setSaupnum(DTO.getSaupnum());
        tbRp940.setPostno(DTO.getPostno());
        tbRp940.setAddress1(DTO.getAddress1());
        tbRp940.setAddress2(DTO.getAddress2());
        tbRp940.setAppflag(DTO.getAppflag());
        return tbRp940;

    }

}
