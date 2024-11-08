package mes.app.order_status;


import mes.app.order_status.service.OrderStatusService;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_DA006W;
import mes.domain.model.AjaxResult;
import org.aspectj.weaver.AjAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/order_status")
public class OrderStatusController {

    @Autowired
    OrderStatusService orderStatusService;

    @GetMapping("/read")
    public AjaxResult orderStatusRead(Authentication auth) {
        AjaxResult result = new AjaxResult();

        try {
            // 로그인한 사용자 정보에서 이름(perid) 가져오기
            User user = (User) auth.getPrincipal();
            String perid = user.getFirst_name(); // 이름을 가져옴

            List<Map<String, Object>> orderStatusList = orderStatusService.getOrderStatusByOperid(perid);

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

}
