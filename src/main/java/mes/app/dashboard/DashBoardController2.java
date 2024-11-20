package mes.app.dashboard;

import mes.app.dashboard.service.DashBoardService2;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

        // 작년 진행구분(ordflag)별 데이터 개수
        List<Map<String, Object>> LastYearCnt = this.dashBoardService2.LastYearCnt(spjangcd);
        // 올해 진행구분(ordflag)별 데이터 개수
        List<Map<String, Object>> ThisYearCnt = this.dashBoardService2.ThisYearCnt(spjangcd);

        // 올해 월별 데이터
        List<Map<String, Object>> ThisYearCntOfMonth = this.dashBoardService2.ThisYearCntOfMonth(spjangcd);
        // 작년 월별 데이터
        List<Map<String, Object>> LastYearCntOfMonth = this.dashBoardService2.LastYearCntOfMonth(spjangcd);
        // 올해 이번달 일별 데이터 개수
        List<Map<String, Object>> ThisMonthCntOfDate = this.dashBoardService2.ThisMonthCntOfDate(spjangcd);
        // 작년 동일 월 일별 데이터 개수
        List<Map<String, Object>> LastMonthCntOfDate = this.dashBoardService2.LastMonthCntOfDate(spjangcd);

        AjaxResult result = new AjaxResult();
        Map<String, Object> items = new HashMap<String, Object>();
        items.put("LastYearCnt", LastYearCnt);
        items.put("ThisYearCnt", ThisYearCnt);
        items.put("ThisYearCntOfMonth", ThisYearCntOfMonth);
        items.put("LastYearCntOfMonth", LastYearCntOfMonth);
        items.put("ThisMonthCntOfDate", ThisMonthCntOfDate);
        items.put("LastMonthCntOfDate", LastMonthCntOfDate);
        result.data = items;

        return result;
    }

}
