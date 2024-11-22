package mes.app.order_status;


import mes.app.order_status.service.OrderStatusService;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_DA006W_PK;
import mes.domain.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/order_status")
public class OrderStatusController {

    @Autowired
    OrderStatusService orderStatusService;

    @GetMapping("/read")
    public AjaxResult orderStatusRead(@RequestParam(value = "search_spjangcd", required = false) String searchSpjangcd,
                                      Authentication auth) {
        AjaxResult result = new AjaxResult();

        try {
            // 로그인한 사용자 정보에서 이름(perid) 가져오기
            User user = (User) auth.getPrincipal();
            String perid = user.getFirst_name(); // 이름을 가져옴
            String spjangcd = searchSpjangcd;
            List<Map<String, Object>> orderStatusList = orderStatusService.getOrderStatusByOperid(perid, spjangcd);

            result.success = true;
            result.data = orderStatusList;
            result.message = "데이터 조회 성공";

        } catch (Exception e) {
            // 오류 발생 시 실패 상태 설정
            result.success = false;
            result.message = "데이터를 가져오는 중 오류가 발생했습니다.";
        }

        return result;
    }

    @GetMapping("/ModalRead")
    public AjaxResult ModalRead(@RequestParam(required = false) String searchTerm) {
        AjaxResult result = new AjaxResult();

        try {
            List<Map<String, Object>> modalList = orderStatusService.getModalListByClientName(searchTerm);

            result.success = true;
            result.data = modalList;
            result.message = "데이터 조회 성공";

        } catch (Exception e) {
            // 오류 발생 시 실패 상태 설정
            result.success = false;
            result.message = "데이터를 가져오는 중 오류가 발생했습니다.";
        }

        return result;
    }

    @GetMapping("/searchData")
    public ResponseEntity<Map<String, Object>> searchData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String searchCltnm,
            @RequestParam(required = false) String searchtketnm,
            @RequestParam(required = false) String searchstate) {

        // 검색 결과를 서비스에서 가져오기
        List<Map<String, Object>> result = orderStatusService.searchData(startDate, endDate, searchCltnm, searchtketnm, searchstate);

        // 응답 데이터를 "data" 키로 래핑하여 JSON 형식으로 반환
        Map<String, Object> response = new HashMap<>();
        response.put("data", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getOrdtext")
    public AjaxResult getOrdtext(@RequestParam("reqdate") String reqdate, @RequestParam("remark") String remark) {
        System.out.println("요청사항 팝업 들어옴: reqdate = " + reqdate + ", remark = " + remark);

        AjaxResult result = new AjaxResult();
        try {
            String ordtextData = orderStatusService.getOrdtextByParams(reqdate, remark);

            result.success = true;
            result.data = ordtextData;
            result.message = "데이터 조회 성공";

        } catch (Exception e) {
            result.success = false;
            result.message = "데이터를 가져오는 중 오류가 발생했습니다.";
            e.printStackTrace(); // 오류 로그 출력
        }

        return result;
    }

    @GetMapping("/readCalenderGrid")
    public AjaxResult getList(@RequestParam(value = "search_spjangcd", required = false) String searchSpjangcd,
                              @RequestParam(value = "search_startDate", required = false) String searchStartDate,
                              @RequestParam(value = "search_endDate", required = false) String searchEndDate,
                              @RequestParam(value = "search_type", required = false) String searchType,
                              Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();  // 유저 사업자번호(id)
        Map<String, Object> userInfo = orderStatusService.getUserInfo(username);
        TB_DA006W_PK tbDa006WPk = new TB_DA006W_PK();
        tbDa006WPk.setSpjangcd(searchSpjangcd);
        tbDa006WPk.setCustcd((String) userInfo.get("custcd"));
        String saupnum = (String) userInfo.get("saupnum");
        String search_startDate = (searchStartDate).replaceAll("-","");
        String search_endDate = (searchEndDate).replaceAll("-","");
        List<Map<String, Object>> items = this.orderStatusService.getOrderList(tbDa006WPk,
                search_startDate, search_endDate, searchType, saupnum);
        for (Map<String, Object> item : items) {
            if (item.get("ordflag").equals("0")) {
                item.remove("ordflag");
                item.put("ordflag", "주문의뢰");
            } else if (item.get("ordflag").equals("1")) {
                item.remove("ordflag");
                item.put("ordflag", "주문확인");
            } else if (item.get("ordflag").equals("2")) {
                item.remove("ordflag");
                item.put("ordflag", "주문확정");
            } else if (item.get("ordflag").equals("3")) {
                item.remove("ordflag");
                item.put("ordflag", "제작진행");
            } else if (item.get("ordflag").equals("4")) {
                item.remove("ordflag");
                item.put("ordflag", "출고");
            }
            // 날짜 형식 변환 (reqdate)
            if (item.containsKey("reqdate")) {
                String setupdt = (String) item.get("reqdate");
                if (setupdt != null && setupdt.length() == 8) {
                    String formattedDate = setupdt.substring(0, 4) + "-" + setupdt.substring(4, 6) + "-" + setupdt.substring(6, 8);
                    item.put("reqdate", formattedDate);
                }
            }
            // 날짜 형식 변환 (deldate)
            if (item.containsKey("deldate")) {
                String setupdt = (String) item.get("deldate");
                if (setupdt != null && setupdt.length() == 8) {
                    String formattedDate = setupdt.substring(0, 4) + "-" + setupdt.substring(4, 6) + "-" + setupdt.substring(6, 8);
                    item.put("deldate", formattedDate);
                }
            }
        }
        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @GetMapping("/isAdmin")
    public AjaxResult isAdmin(Authentication auth) {
        User user = (User) auth.getPrincipal();
        int userCode = user.getUserProfile().getUserGroup().getId();
        // 사용자의 권한이 일반거래처(code값 : 35)인지확인
        String userAuth;
        if(userCode == 35){
            userAuth = "nomal";
        }else {
            userAuth = "admin";
        }
        AjaxResult result = new AjaxResult();
        result.data = userAuth;
        return result;
    }

    @GetMapping("/initDatas")
    public AjaxResult initDatas(@RequestParam(value = "search_spjangcd", required = false) String searchSpjangcd,
                                Authentication auth){
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = orderStatusService.getUserInfo(username);
        TB_DA006W_PK tbDa006WPk = new TB_DA006W_PK();
        tbDa006WPk.setSpjangcd(searchSpjangcd);
        tbDa006WPk.setCustcd((String) userInfo.get("custcd"));
        String cltcd = (String) userInfo.get("cltcd");
        List<Map<String, Object>> items = this.orderStatusService.initDatas(tbDa006WPk, cltcd);
        AjaxResult result = new AjaxResult();
        result.data = items;
        return result;
    }
}
