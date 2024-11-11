package mes.app.account;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import mes.app.MailService;
import mes.app.account.service.TB_RP940_Service;
import mes.app.account.service.TB_RP945_Service;
import mes.app.account.service.TB_XClientService;
import mes.app.account.service.UserProfileService;
import mes.app.system.service.AuthListService;
import mes.app.system.service.UserService;
import mes.domain.DTO.UserCodeDto;
import mes.domain.entity.*;
import mes.domain.entity.actasEntity.TB_XA012;
import mes.domain.entity.actasEntity.TB_XCLIENT;
import mes.domain.entity.actasEntity.TB_XCLIENTId;
import mes.domain.repository.*;
import mes.domain.repository.actasRepository.TB_XA012Repository;
import mes.domain.repository.actasRepository.TB_XClientRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import mes.domain.model.AjaxResult;
import mes.domain.security.CustomAuthenticationToken;
import mes.domain.security.Pbkdf2Sha256;
import mes.domain.services.AccountService;
import mes.domain.services.SqlRunner;


@RestController
public class AccountController {

	@Autowired
	AccountService accountService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserCodeRepository userCodeRepository;

	@Autowired
	SqlRunner sqlRunner;

	@Autowired
	TB_RP940_Service tbRp940Service;

	@Autowired
	TB_RP945_Service tbRp945Service;


	@Autowired
	TB_RP940Repository tb_rp940Repository;

	@Autowired
	TB_RP945Repository tb_rp945Repository;

	@Autowired
	MailService emailService;

	@Autowired
	AuthListService authListService;

	@Autowired
	TB_RP920Repository tbRp920Repository;
	@Autowired
	TB_XA012Repository tbXA012Repository;
	@Autowired
	TB_XClientService tbXClientService;
	@Autowired
	TB_XClientRepository tbXClientRepository;
	@Autowired
	JdbcTemplate jdbcTemplate;


	@Resource(name="authenticationManager")
	private AuthenticationManager authManager;
	@Autowired
	private UserGroupRepository userGroupRepository;

	private Boolean flag;
	private Boolean flag_pw;

	private final ConcurrentHashMap<String, String> tokenStore = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<String, Long> tokenExpiry = new ConcurrentHashMap<>();
    @Autowired
    private UserService userService;

	@GetMapping("/login")
	public ModelAndView loginPage(
			HttpServletRequest request,
			HttpServletResponse response,
			HttpSession session, Authentication auth) {
		ModelAndView mv = new ModelAndView("login");

		Map<String, Object> userInfo = new HashMap<String, Object>();
		Map<String, Object> gui = new HashMap<String, Object>();

		mv.addObject("userinfo", userInfo);

		mv.addObject("gui", gui);
		if(auth!=null) {
			SecurityContextLogoutHandler handler =  new SecurityContextLogoutHandler();
			handler.logout(request, response, auth);
		}

		return mv;
	}

	@GetMapping("/logout")
	public void logout(
			HttpServletRequest request
			, HttpServletResponse response) throws IOException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		SecurityContextLogoutHandler handler =  new SecurityContextLogoutHandler();

		this.accountService.saveLoginLog("logout", auth);

		handler.logout(request, response, auth);
		response.sendRedirect("/login");
	}

	@PostMapping("/login")
	public AjaxResult postLogin(
			@RequestParam("username") final String username,
			@RequestParam("password") final String password,
			final HttpServletRequest request) throws UnknownHostException {
		// 여기로 들어오지 않음.


		//System.out.print(list);


		AjaxResult result = new AjaxResult();

		HashMap<String, Object> data = new HashMap<String, Object>();
		result.data = data;

		UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
		CustomAuthenticationToken auth = null;
		try{
			auth = (CustomAuthenticationToken)authManager.authenticate(authReq);
		}catch (AuthenticationException e){
			//e.printStackTrace();
			data.put("code", "NOUSER");
			return result;
		}

		if(auth!=null) {
			User user = (User)auth.getPrincipal();
			user.getActive();
			data.put("code", "OK");

			try {
				this.accountService.saveLoginLog("login", auth);
			} catch (UnknownHostException e) {
				// Handle the exception (e.g., log it)
				e.printStackTrace();
			}
		} else {
			result.success=false;
			data.put("code", "NOID");
		}

		SecurityContext sc = SecurityContextHolder.getContext();
		sc.setAuthentication(auth);

		HttpSession session = request.getSession(true);
		session.setAttribute("SPRING_SECURITY_CONTEXT", sc);

		return result;
	}

	@GetMapping("/account/myinfo")
	public AjaxResult getUserInfo(Authentication auth){
		User user = (User)auth.getPrincipal();
		AjaxResult result = new AjaxResult();

		Map<String, Object> dicData = new HashMap<String, Object>();
		dicData.put("login_id", user.getUsername());
		dicData.put("name", user.getUserProfile().getName());
		result.data = dicData;
		return result;
	}

	@PostMapping("/account/myinfo/password_change")
	public AjaxResult userPasswordChange(
			@RequestParam("name") final String name,
			@RequestParam("loginPwd") final String loginPwd,
			@RequestParam("loginPwd2") final String loginPwd2,
			Authentication auth
	) {

		User user = (User)auth.getPrincipal();
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

		user.setPassword(Pbkdf2Sha256.encode(loginPwd2));
		//user.getUserProfile().setName(name);
		this.userRepository.save(user);

		String sql = """
        	update user_profile set 
        	"Name"=:name, _modified = now(), _modifier_id=:id 
        	where id=:id 
        """;

		MapSqlParameterSource dicParam = new MapSqlParameterSource();
		dicParam.addValue("name", name);
		dicParam.addValue("id", user.getId());
		this.sqlRunner.execute(sql, dicParam);


		return result;
	}


	/***
	 *  아이디 중복 확인
	 * **/
	@PostMapping("/useridchk")
	public AjaxResult IdChk(@RequestParam("userid") final String userid){

		AjaxResult result = new AjaxResult();


		Optional<User> user = userRepository.findByUsername(userid);


		if(!user.isPresent()){

			result.success = true;
			result.message = "사용할 수 있는 계정입니다.";
			return result;

		}else {
			result.success = false;
			result.message = "중복된 계정이 존재합니다.";
			return result;
		}


	}

	@PostMapping("/Register/save")
	@Transactional
	public AjaxResult RegisterUser(
			@RequestParam(value = "cltnm") String cltnm, // 업체명
			@RequestParam(value = "prenm") String prenm, // 대표자
			@RequestParam(value = "biztypenm") String biztypenm, // 업태
			@RequestParam(value = "bizitemnm") String bizitemnm, // 종목
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "tel", required = false) String tel,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "id") String id,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "postno") String postno,
			@RequestParam(value = "address1") String address1,
			@RequestParam(value = "address2") String address2
	) {
		AjaxResult result = new AjaxResult();

		try {
			if (flag) {
				// 사용자 저장
				User user = User.builder()
						.username(id)
						.password(Pbkdf2Sha256.encode(password))
						.email(email)
						.first_name(prenm)
						.last_name("")
						.tel(tel)
						.active(true)
						.is_staff(false)
						.date_joined(new Timestamp(System.currentTimeMillis()))
						.superUser(false)
						.phone(phone)

						.build();

				userService.save(user); // User 저장

				// UserProfile 저장 (JDBC 사용)
				String sql = "INSERT INTO user_profile (_created, _creater_id, User_id, lang_code, Name, UserGroup_id) VALUES (?, ?, ?, ?, ?, ?)";
				jdbcTemplate.update(sql,
						new Timestamp(System.currentTimeMillis()), // 현재 시간
						user.getId(), // _creater_id (현재 로그인한 사용자 ID)
						user.getId(), // User_id (저장할 사용자 ID)
						"ko-KR", // lang_code (예: 한국어)
						prenm, // Name (사용자 이름)
						1 // UserGroup_id (저장할 사용자 그룹 ID: 1)
				);


				// TB_XA012에서 custcd와 spjangcd로 조회
				String custcd = "SWSPANEL";
				List<String> spjangcds = Arrays.asList("ZZ", "YY");

				List<TB_XA012> tbX_A012List = tbXA012Repository.findByCustcdAndSpjangcds(custcd, spjangcds);
				if (tbX_A012List.isEmpty()) {
					result.success = false;
					result.message = "custcd 및 spjangcd에 해당하는 데이터를 찾을 수 없습니다.";
					return result;
				}

				String fullAddress = address1 + (address2 != null && !address2.isEmpty() ? " " + address2 : ""); // 상세주소가 있을 경우 공백으로 구분하여 결합

				// TB_XCLIENT 저장
				String maxCltcd = tbXClientRepository.findMaxCltcd(); // 최대 cltcd 조회
				String newCltcd = generateNewCltcd(maxCltcd); // 새로운 cltcd 생성

				TB_XCLIENT tbXClient = TB_XCLIENT.builder()
						.saupnum(id) // 사업자번호
						.prenm(prenm) // 대표자명
						.cltnm(cltnm)	//업체명
						.biztypenm(biztypenm) // 업태명
						.bizitemnm(bizitemnm) // 종목명
						.zipcd(postno) // 우편번호
						.cltadres(fullAddress) // 주소
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
						/*.nation("KR")               // nation = "KR"*/
						.clttype(String.valueOf(2))                 // clttype = 2 (거래구분)
						.cltynm(String.valueOf(0))                  // cltynm = 0 (약명)
						.build();

				tbXClientService.save(tbXClient); // TB_XCLIENT 저장

				result.success = true;
				result.message = "등록이 완료되었습니다.";
				flag = false;
			} else {
				result.success = false;
				result.message = "코드가 인증되지 않았습니다.";
			}
		} catch (Exception e) {
			result.success = false;
			System.out.println("오류 발생: " + e.getMessage());
			System.out.println(e);
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

	/*@PostMapping("/user-auth/searchAccount")
	public AjaxResult IdSearch(@RequestParam("usernm") final String usernm,
							   @RequestParam("mail") final String mail){

		AjaxResult result = new AjaxResult();

		List<String> user = userRepository.findByFirstNameAndEmailNative(usernm, mail);

		if(!user.isEmpty()){
			result.success = true;
			result.data = user;
		}else {
			result.success = false;
			result.message = "해당 사용자가 존재하지 않습니다.";
		}
		return result;
	}*/

	@PostMapping("/user-auth/searchAccount")
	public AjaxResult IdSearch(@RequestParam("usernm") final String usernm,
							   @RequestParam("userid1") final String userid1) {

		AjaxResult result = new AjaxResult();

		// 사업자 번호와 대표자를 기반으로 사용자 검색
		List<String> user = userRepository.findByFirstNameAndBusinessNumberNative(usernm, userid1);

		if (!user.isEmpty()) {
			result.success = true;
			result.data = user;
		} else {
			result.success = false;
			result.message = "해당 사용자가 존재하지 않습니다.";
		}
		return result;
	}

	@PostMapping("/user-auth/AuthenticationEmail")
	public AjaxResult PwSearch(@RequestParam("usernm") final String usernm,
							   @RequestParam("mail") final String mail){

		AjaxResult result = new AjaxResult();

		if(usernm.equals("empty")){
			sendEmailLogic(mail, "신규사용자");

			result.success = true;
			result.message = "인증 메일이 발송되었습니다.";
			return result;
		}

		int exists = userRepository.existsByUsernameAndEmail(usernm, mail);
		boolean flag = exists > 0;

		if(flag) {
			sendEmailLogic(mail, usernm);

			result.success = true;
			result.message = "인증 메일이 발송되었습니다.";
		}else {
			result.success = false;
			result.message = "해당 사용자가 존재하지 않습니다.";
		}

		return result;
	}



	private void sendEmailLogic(String mail, String usernm){
		Random random = new Random();
		int randomNum = 100000 + random.nextInt(900000); // 100000부터 999999까지의 랜덤 난수 생성
		String verificationCode = String.valueOf(randomNum); // 정수를 문자열로 변환
		emailService.sendVerificationEmail(mail, usernm, verificationCode);

		tokenStore.put(mail, verificationCode);
		tokenExpiry.put(mail, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3));

	}

	@PostMapping("/user-auth/SaveAuthenticationEmail")
	public AjaxResult saveMail(@RequestParam("usernm") final String usernm,
							   @RequestParam("prenm") final String prenm,
							   @RequestParam("mail") final String mail) {

		AjaxResult result = new AjaxResult();

		if (usernm.equals("empty")) {
			saveEmailLogic(mail, prenm);

			result.success = true;
			result.message = "인증 메일이 발송되었습니다.";
			return result;
		}

		int exists = userRepository.existsByUsernameAndEmail(usernm, mail);
		boolean flag = exists > 0;

		if (flag) {
			saveEmailLogic(mail, usernm);

			result.success = true;
			result.message = "인증 메일이 발송되었습니다.";
		} else {
			result.success = false;
			result.message = "해당 사용자가 존재하지 않습니다.";
		}

		return result;
	}


	private void saveEmailLogic(String mail, String usernm){
		Random random = new Random();
		int randomNum = 100000 + random.nextInt(900000); // 100000부터 999999까지의 랜덤 난수 생성
		String verificationCode = String.valueOf(randomNum); // 정수를 문자열로 변환
		emailService.saveVerificationEmail(mail, usernm, verificationCode);

		tokenStore.put(mail, verificationCode);
		tokenExpiry.put(mail, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(3));

	}

	@PostMapping("/user-auth/verifyCode")
	public AjaxResult verifyCode(@RequestParam("code") final String code,
								 @RequestParam("mail") final String mail,
								 @RequestParam("password") final String password,
								 @RequestParam("userid") final String userid
	){

		AjaxResult result = new AjaxResult();
		result = verifyAuthenticationCode(code, mail);

		if(result.success){
			String pw = Pbkdf2Sha256.encode(password);


			userRepository.PasswordChange(pw, userid);


			result.success = true;
			result.message = "비밀번호가 변경되었습니다.";

			return result;
		}else{
			return result;
		}

		/*AjaxResult result = new AjaxResult();


		String storedToken = tokenStore.get(mail);

		if(storedToken != null && storedToken.equals(code)){
			long expiryTime = tokenExpiry.getOrDefault(mail, 0L);
			if(System.currentTimeMillis() > expiryTime){
				result.success = false;
				result.message = "인증 코드가 만료되었습니다.";
				tokenStore.remove(mail);
				tokenExpiry.remove(mail);
			} else {

				String pw = Pbkdf2Sha256.encode(password);


				userRepository.PasswordChange(pw, userid);


				result.success = true;
				result.message = "비밀번호가 변경되었습니다.";
			}
		}else {
			result.success = false;
			result.message = "인증 코드가 유효하지 않습니다.";
		}*/


	}

	private AjaxResult verifyAuthenticationCode(String code, String mail){

		AjaxResult result = new AjaxResult();

		String storedToken = tokenStore.get(mail);
		if(storedToken != null && storedToken.equals(code)){
			long expiryTime = tokenExpiry.getOrDefault(mail, 0L);
			if(System.currentTimeMillis() > expiryTime){
				result.success = false;
				result.message = "인증 코드가 만료되었습니다.";
				tokenStore.remove(mail);
				tokenExpiry.remove(mail);
			} else {
				result.success = true;
				result.message = "비밀번호가 변경되었습니다.";
			}
		}else{
			result.success = false;
			result.message = "인증 코드가 유효하지 않습니다.";
		}
		return result;
	}


	@GetMapping("/user-codes/parent")
	public List<UserCodeDto> getUserCodeByParentId(@RequestParam Integer parentId){

		List<UserCode> list = userCodeRepository.findByParentId(parentId);

		List<UserCodeDto> dtoList = list.stream()
				.map(userCode -> new UserCodeDto(
						userCode.getId(),
						userCode.getCode(),
						userCode.getValue()
				))
				.toList();

		return dtoList;
	}

	@GetMapping("/user-auth/type")
	public List<UserGroup> getUserAuthTypeAll(){
		return userGroupRepository.findAll();

	}

	@GetMapping("/user-codes/code")
	public List<UserCodeDto> getUserCodesByCode(@RequestParam Integer code) {

		List<UserCode> list = userCodeRepository.findByParentId(code);

		List<UserCodeDto> dtoList = list.stream()
				.map(userCode -> new UserCodeDto(
						userCode.getId(),
						userCode.getCode(),
						userCode.getValue()
				)).toList();

		return dtoList;
	}

	@PostMapping("/authentication")
	public AjaxResult Authentication(@RequestParam(value = "AuthenticationCode") String AuthenticationCode,
									 @RequestParam(value = "email", required = false) String email,
									 @RequestParam String type
	){

		AjaxResult result = verifyAuthenticationCode(AuthenticationCode, email);

		if(type.equals("new")){
			if(result.success){
				flag = true;
				result.message = "인증되었습니다.";

			}

		}else{
			if(result.success){
				flag_pw = true;
				result.message = "인증되었습니다.";
			}
		}

		return result;
	}

}