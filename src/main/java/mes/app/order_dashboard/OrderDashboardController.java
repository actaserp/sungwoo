package mes.app.order_dashboard;

import mes.app.order_dashboard.service.OrderDashboardService;
import mes.app.request.request.service.RequestService;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_DA006W_PK;
import mes.domain.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orderDashboard")
public class OrderDashboardController {

    @Autowired
    private OrderDashboardService orderDashboardService;

    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "search_startDate", required = false) String searchStartDate,
                              @RequestParam(value = "search_endDate", required = false) String searchEndDate,
                              @RequestParam(value = "search_type", required = false) String searchType,
             Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = orderDashboardService.getUserInfo(username);
        TB_DA006W_PK tbDa006WPk = new TB_DA006W_PK();
        tbDa006WPk.setSpjangcd("ZZ");
        tbDa006WPk.setCustcd((String) userInfo.get("custcd"));
        List<Map<String, Object>> items = this.orderDashboardService.getOrderList(tbDa006WPk,
                searchStartDate, searchEndDate, searchType);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }



}
