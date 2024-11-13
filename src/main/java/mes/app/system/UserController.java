package mes.app.system;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import mes.app.UtilClass;
import mes.app.account.service.TB_RP945_Service;
import mes.app.account.service.TB_XClientService;
import mes.domain.DTO.TB_RP945Dto;
import mes.domain.entity.*;
import mes.domain.entity.actasEntity.TB_XA012;
import mes.domain.entity.actasEntity.TB_XCLIENT;
import mes.domain.entity.actasEntity.TB_XCLIENTId;
import mes.domain.repository.*;
import mes.domain.repository.actasRepository.TB_XA012Repository;
import mes.domain.repository.actasRepository.TB_XClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mes.app.system.service.UserService;
import mes.domain.model.AjaxResult;
import mes.domain.security.Pbkdf2Sha256;
import mes.domain.services.CommonUtil;
import mes.domain.services.SqlRunner;

@RestController
@RequestMapping("/api/system/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RelationDataRepository relationDataRepository;

	@Autowired
	SqlRunner sqlRunner;
	@Autowired
	private TB_RP940Repository tB_RP940Repository;
	@Autowired
	private TB_RP945Repository tB_RP945Repository;

	@Autowired
	private UserCodeRepository userCodeRepository;

	@Autowired
	TB_XClientService tbXClientService;

	@Autowired
	TB_XClientRepository tbXClientRepository;
	@Autowired
	TB_RP945_Service tbRp945Service;

	@Autowired
	TB_XA012Repository tbXA012Repository;

	@GetMapping("/read")
	public AjaxResult getUserList(@RequestParam(value = "cltnm", required = false) String cltnm, // 업체명
								  @RequestParam(value = "prenm", required = false) String prenm, // 대표자
								  @RequestParam(value = "biztypenm", required = false) String biztypenm, // 업태
								  @RequestParam(value = "bizitemnm", required = false) String bizitemnm, // 종목
								  @RequestParam(value = "email", required = false) String email,
								  Authentication auth){
		AjaxResult result = new AjaxResult();
		User user = (User)auth.getPrincipal();
		boolean superUser = user.getSuperUser();

		if (!superUser) {
			superUser = user.getUserProfile().getUserGroup().getCode().equals("dev");
		}

		List<Map<String, Object>> items = this.userService.getUserList(superUser, cltnm, prenm, biztypenm, bizitemnm, email);

		result.data = items;

		return result;
	}

	@GetMapping("/detail")
	public AjaxResult getUserDetail(@RequestParam(value = "id", required = false) String id) {
		AjaxResult result = new AjaxResult();

		try {
			if (id != null && !id.isEmpty()) {
				// id로 특정 사용자 정보 조회
				Map<String, Object> userDetail = userService.getUserDetailById(id);
				result.success = true;
				result.data = userDetail;
				result.message = "데이터 조회 성공";
			} else {
				result.success = false;
				result.message = "유효한 ID가 제공되지 않았습니다.";
			}
		} catch (Exception e) {
			result.success = false;
			result.message = "데이터를 가져오는 중 오류가 발생했습니다.";
		}

		return result;
	}

	// 사용자 그룹 조회
	@GetMapping("/user_grp_list")
	public AjaxResult getUserGrpList(
			@RequestParam(value="id") Integer id,
			HttpServletRequest request) {

		List<Map<String, Object>> items = this.userService.getUserGrpList(id);
		AjaxResult result = new AjaxResult();
		result.data = items;
		return result;
	}

	@PostMapping("/save")
	@Transactional
	public AjaxResult saveUser(
			@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "cltnm") String cltnm,
			@RequestParam(value = "prenm") String prenm,
			@RequestParam(value = "biztypenm") String biztypenm,
			@RequestParam(value = "bizitemnm") String bizitemnm,
			@RequestParam(value = "tel") String tel,
			@RequestParam(value = "Phone") String phone,
			@RequestParam(value = "userid") String userid,
			@RequestParam(value = "email") String email,
			@RequestParam(value="agencycd", required = false) String agencycd,
			@RequestParam(value = "is_active") boolean isActive,
			@RequestParam(value = "postno") String postno,
			@RequestParam(value = "address1") String address1,
			@RequestParam(value = "placeAddress") String placeAddress,
			@RequestParam(value="UserGroup_id", required = false) Integer UserGroup_id,
			Authentication auth
	) {
		AjaxResult result = new AjaxResult();
		System.out.println("저장들어옴");
		try {
			// 데이터 저장 로직
			String sql = null;
			User user = null;
			User loginUser = (User)auth.getPrincipal();
			Timestamp today = new Timestamp(System.currentTimeMillis());
			MapSqlParameterSource dicParam = new MapSqlParameterSource();

			if(id==null){

				Optional<User> u1 = this.userRepository.findByUsername(userid);

				boolean username_chk = u1.isEmpty();

				if (username_chk == false){
					result.success = false;
					result.message = "중복된 아이디가 존재합니다.";
					return result;
				}
				user = new User();

				user.setSuperUser(false);
				user.setLast_name("");
				user.setIs_staff(false);

				dicParam.addValue("loginUser", loginUser.getId());

				sql = """
						INSERT INTO ERP_SWSPANEL1.dbo.user_profile
							(_created, _creater_id, User_id, lang_code, Name, UserGroup_id)
						VALUES 
							(GETDATE(), :loginUser, :User_id, :lang_code, :name, :UserGroup_id)
					""";
			}else {

				user = this.userRepository.getUserById(id);
				boolean superchk =  user.getSuperUser();
				if(superchk){
					result.success = false;
					result.message = "슈퍼유저 계정은 수정이 불가합니다.";
					return result;
				}

				sql = """
					update user_profile set
					     	"lang_code" = :lang_code, "Name" = :name
					     	,   "UserGroup_id" = :UserGroup_id
					where "User_id" = :User_id
		        """;
			}

			user.setUsername(userid);
			user.setFirst_name(prenm);
			user.setEmail(email);
			user.setDate_joined(today);
			user.setActive(true);
			user.setTel(tel);
			user.setPhone(phone);
			user.setActive(isActive);
			user.setAgencycd(agencycd);


			user = this.userRepository.save(user);
			System.out.println("user 저장 완료");

			dicParam.addValue("name", prenm);
			dicParam.addValue("UserGroup_id", UserGroup_id);
			dicParam.addValue("lang_code", "ko-KR");
			dicParam.addValue("User_id", user.getId());


			this.sqlRunner.execute(sql, dicParam);
			System.out.println("user_profile 저장 완료");

			// TB_XA012에서 custcd와 spjangcd로 조회
			String custcd = "SWSPANEL";
			List<String> spjangcds = Arrays.asList("ZZ", "YY");

			List<TB_XA012> tbX_A012List = tbXA012Repository.findByCustcdAndSpjangcds(custcd, spjangcds);
			if (tbX_A012List.isEmpty()) {
				result.success = false;
				result.message = "custcd 및 spjangcd에 해당하는 데이터를 찾을 수 없습니다.";
				return result;
			}

			// TB_XCLIENT 저장
			String maxCltcd = tbXClientRepository.findMaxCltcd(); // 최대 cltcd 조회
			String newCltcd = generateNewCltcd(maxCltcd); // 새로운 cltcd 생성

			// 도로명 주소 또는 지번 주소 중 하나만 설정
			String finalAddress = (address1 != null && !address1.isEmpty()) ? address1 : placeAddress;

			// 기존 TB_XCLIENT 데이터가 있는지 확인
			Optional<TB_XCLIENT> existingClientOpt = tbXClientRepository.findBySaupnum(userid);

			TB_XCLIENT tbXClient;
			if (existingClientOpt.isPresent()) {
				// 기존 데이터가 있을 경우 업데이트
				tbXClient = existingClientOpt.get();
				tbXClient.setPrenm(prenm);
				tbXClient.setCltnm(cltnm);
				tbXClient.setBiztypenm(biztypenm);
				tbXClient.setBizitemnm(bizitemnm);
				tbXClient.setZipcd(postno);
				tbXClient.setCltadres(finalAddress);
				tbXClient.setTelnum(tel);
				tbXClient.setHptelnum(phone);
				tbXClient.setAgneremail(email);
			} else {
				// 새로운 TB_XCLIENT 객체 생성
				tbXClient = TB_XCLIENT.builder()
						.saupnum(userid) // 사업자번호
						.prenm(prenm) // 대표자명
						.cltnm(cltnm) // 업체명
						.biztypenm(biztypenm) // 업태명
						.bizitemnm(bizitemnm) // 종목명
						.zipcd(postno) // 우편번호
						.cltadres(finalAddress) // 주소
						.telnum(tel) // 전화번호
						.hptelnum(phone) // 핸드폰번호
						.agneremail(email) // 담당자 email
						.id(new TB_XCLIENTId(custcd, newCltcd))

						// 기본값 설정된 필드들
						.rnumchk(String.valueOf(0))                 // rnumchk = 0
						.corpperclafi(String.valueOf(1))            // corpperclafi = 1 (법인구분)
						.cltdv(String.valueOf(1))                   // cltdv = 1 (거래처구분)
						.prtcltnm(cltnm) 							   // prtcltnm = "인쇄 거래처명 - 거래처명"
						.foreyn(String.valueOf(0))                  // foreyn = 0
						.relyn(String.valueOf(1))                   // relyn = 1
						.bonddv(String.valueOf(0))                  // bonddv = 0
						/*.nation("KR")               				   // nation = "KR"*/
						.clttype(String.valueOf(2))                 // clttype = 2 (거래구분)
						.cltynm(String.valueOf(0))                  // cltynm = 0 (약명)
						.build();
			}

			System.out.println("TB_XCLIENT 저장 시작");
			tbXClientService.save(tbXClient);// TB_XCLIENT 저장
			System.out.println("TB_XCLIENT 저장 완료");

			result.success = true;
			result.message = "사용자 정보가 성공적으로 저장되었습니다.";
		} catch (Exception e) {
			result.success = false;
			result.message = "사용자 정보를 저장하는 중 오류가 발생했습니다.";
		}
		return result;
	}

	// 새로운 cltcd 생성 메서드
	private String generateNewCltcd(String maxCltcd) {
		int newNumber = 1; // 기본값
		// 최대 cltcd 값이 null이 아니고 "SW"로 시작하는 경우
		if (maxCltcd != null && maxCltcd.startsWith("SW")) {
			String numberPart = maxCltcd.substring(2); // "SW"를 제외한 부분
			newNumber = Integer.parseInt(numberPart) + 1; // 숫자 증가
		}
		// 새로운 cltcd 생성: "SW" 접두사와 5자리 숫자로 포맷
		return String.format("SW%05d", newNumber);
	}

	// user 삭제
	@Transactional
	@PostMapping("/delete")
	public AjaxResult deleteUser(@RequestParam("id") String id,
								 @RequestParam(value = "username", required = false) String username
	) {

		UtilClass util = new UtilClass();
		System.out.println(util.removeBrackers(username));
		Optional<User> user = userRepository.findByUsername(util.removeBrackers(username));

		if(user.isPresent()){
			tB_RP940Repository.deleteByUserid(user.get().getUsername());
			tB_RP945Repository.deleteByUserid(user.get().getUsername());
		}

		Integer userid  = Integer.parseInt(util.removeBrackers(id))  ;

		this.userRepository.deleteById(userid);
		AjaxResult result = new AjaxResult();
		return result;
	}


	@PostMapping("/modfind")
	public AjaxResult getBtId(@RequestBody String userid){

		///userid = new UtilClass().removeBrackers(userid);
		AjaxResult result = new AjaxResult();

		List<TB_RP945> tbRp945 =  tB_RP945Repository.findByUserid(userid);

		if(!tbRp945.isEmpty()){
			result.success = true;
			result.data = tbRp945;
		}else{
			result.success = false;
			result.message = "해당 유저에 대한 권한상세정보가 없습니다.";
		}



		return result;
	}


	// user 패스워드 셋팅
	@PostMapping("/passSetting")
	@Transactional
	public AjaxResult userPassSetting(
			@RequestParam(value="id", required = false) Integer id,
			@RequestParam(value="pass1", required = false) String loginPwd,
			@RequestParam(value="pass2", required = false) String loginPwd2,
			Authentication auth
	) {

		User user = null;
		AjaxResult result = new AjaxResult();

		if (StringUtils.hasText(loginPwd)==false | StringUtils.hasText(loginPwd2)==false) {
			result.success=false;
			result.message="The verification password is incorrect.";
			return result;
		}

		if(loginPwd.equals(loginPwd2)==false) {
			result.success=false;
			result.message="The verification password is incorrect.";
			return result;
		}

		user = this.userRepository.getUserById(id);
		user.setPassword(Pbkdf2Sha256.encode(loginPwd));
		this.userRepository.save(user);

		return result;
	}

	@PostMapping("/save_user_grp")
	@Transactional
	public AjaxResult saveUserGrp(
			@RequestParam(value="id") Integer id,
			@RequestBody MultiValueMap<String,Object> Q,
			Authentication auth
	) {

		User user = (User)auth.getPrincipal();;

		AjaxResult result = new AjaxResult();

		List<Map<String, Object>> items = CommonUtil.loadJsonListMap(Q.getFirst("Q").toString());

		List<RelationData> rdList = this.relationDataRepository.findByDataPk1AndTableName1AndTableName2(id,"auth_user", "user_group");

		// 등록된 그룹 삭제
		for (int i = 0; i < rdList.size(); i++) {
			this.relationDataRepository.deleteById(rdList.get(i).getId());
		}

		this.relationDataRepository.flush();
		for (int i = 0; i< items.size(); i++) {

			String check = "";

			if (items.get(i).get("grp_check") != null) {
				check = items.get(i).get("grp_check").toString();
			}

			if (check.equals("Y")) {
				RelationData rd = new RelationData();
				rd.setDataPk1(id);
				rd.setTableName1("auth_user");
				rd.setDataPk2(Integer.parseInt(items.get(i).get("grp_id").toString()));
				rd.setTableName2("user_group");
				rd.setRelationName("auth_user-user_group");
				rd.setChar1("Y");
				rd.set_audit(user);

				this.relationDataRepository.save(rd);
			}
		}

		return result;
	}
}
