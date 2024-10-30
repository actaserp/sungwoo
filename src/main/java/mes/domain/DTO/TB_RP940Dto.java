package mes.domain.DTO;


import lombok.*;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TB_RP940Dto {

    private String compnm; // 업체명
    private String agencyDepartment; // 소속부서
    private String level; // 직급
    private String name; // 성명
    private String phone; // 핸드폰
    private String tel; // 전화번호
    private String email; // 이메일
    private String id; // ID
    private String password; // 비밀번호
    private String saupnum; // 사업자번호
    private String postno; // 우편번호
    private String address1; // 주소
    private String address2; // 상세주소

    private String appflag;

    private OffsetDateTime appdatem;
}


