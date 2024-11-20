package mes.app.dashboard;

import mes.app.dashboard.service.DashBoardService2;
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
@RequestMapping("/api/dashboard2")
public class DashBoardController2 {
    @Autowired
    private DashBoardService2 dashBoardService2;

    // 작년 1월1일부터 작년오늘날자까지 상태별 건수
    @GetMapping("/LastYearCnt")
    private AjaxResult LastYearCnt(@RequestParam(value = "search_spjangcd") String search_spjangcd
                                    , Authentication auth) {
        // 관리자 사용가능 페이지 사업장 코드 선택 로직
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        String spjangcd = dashBoardService2.getSpjangcd(username, search_spjangcd);
        // 사업장 코드 선택 로직 종료 반환 spjangcd 활용

        List<Map<String, Object>> items = this.dashBoardService2.LastYearCnt();

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }
    // 올해 1월 1일부터 오늘 날짜까지의 상태별 건수
    @GetMapping("/ThisYearCnt")
    private AjaxResult ThisYearCnt() {

        List<Map<String, Object>> items = this.dashBoardService2.ThisYearCnt();

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    // 올해 1월 1일부터 오늘 날짜까지의 월별 건수
    @GetMapping("/ThisYearCntOfMonth")
    private AjaxResult ThisYearCntOfMonth() {

        List<Map<String, Object>> items = this.dashBoardService2.ThisYearCntOfMonth();

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    // 작년 1월 1일부터 작년 오늘 날짜까지의 월별 건수
    @GetMapping("/LastYearCntOfMonth")
    private AjaxResult LastYearCntOfMonth(Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = dashBoardService2.getUserInfo(username);
        TB_DA006W_PK tbDa006WPk = new TB_DA006W_PK();
        tbDa006WPk.setSpjangcd("ZZ");
        tbDa006WPk.setCustcd((String) userInfo.get("custcd"));

        List<Map<String, Object>> items = this.dashBoardService2.LastYearCntOfMonth();

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    // 올해 당월 1일 부터 오늘까지 일별 건수
    @GetMapping("/ThisMonthCntOfDate")
    private AjaxResult ThisMonthCntOfDate() {

        List<Map<String, Object>> items = this.dashBoardService2.ThisMonthCntOfDate();

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    // 올해 전월 1일 부터 전월 오늘 날자까지 일별 건수
    @GetMapping("/LastMonthCntOfDate")
    private AjaxResult LastMonthCntOfDate() {

        List<Map<String, Object>> items = this.dashBoardService2.LastMonthCntOfDate();

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }
}
