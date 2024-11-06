package mes.app.order_status;


import mes.app.order_status.service.OrderStatusService;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_DA006W;
import mes.domain.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
