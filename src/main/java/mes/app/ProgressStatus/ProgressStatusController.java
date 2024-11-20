package mes.app.ProgressStatus;


import mes.app.ProgressStatus.service.ProgressStatusService;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ProgressStatus")
public class ProgressStatusController {

    @Autowired
    ProgressStatusService progressStatusService;


    @GetMapping("/read")
    public AjaxResult ProgressStatusRead(Authentication auth) {

        AjaxResult result = new AjaxResult();

        try{
            // 로그인한 사용자 정보에서 이름(perid) 가져오기
            User user = (User) auth.getPrincipal();
            String perid = user.getFirst_name();

            List<Map<String, Object>> progressStatusLis = progressStatusService.getProgressStatusList(perid);
            result.success = true;
            result.data = progressStatusLis;
            result.message = "데이터 조회 성공";

        } catch (Exception e) {
            // 오류 발생 시 실패 상태 설정
            result.success = false;
            result.message = "데이터를 가져오는 중 오류가 발생했습니다.";
        }

        return result;
    }

    @GetMapping("/searchProgress")
    public AjaxResult searchProgress(@RequestParam(value = "search_startDate", required = false) String searchStartDate
                                    , @RequestParam(value = "search_endDate", required = false) String searchEndDate
                                    , @RequestParam(value = "search_remark", required = false) String searchRemark
                                    , Authentication auth) {
        AjaxResult result = new AjaxResult();

        try {
            // 로그인한 사용자 정보에서 이름(perid) 가져오기
            User user = (User) auth.getPrincipal();
            String perid = user.getFirst_name();

            List<Map<String, Object>> progressStatusLis = progressStatusService.searchProgress(searchStartDate, searchEndDate, searchRemark);
            result.success = true;
            result.data = progressStatusLis;
            result.message = "데이터 조회 성공";
        }
        catch (Exception e) {
            // 오류 발생 시 실패 상태 설정
            result.success = false;
            result.message = "데이터를 가져오는 중 오류가 발생했습니다.";
        }
        return result;
    }


    @GetMapping("/getChartData")
    public AjaxResult getChartData(Authentication auth) {
        AjaxResult result = new AjaxResult();

        try {
            User user = (User) auth.getPrincipal();
            String userid = user.getUsername();

            // SQL 쿼리 결과 확인
            List<Map<String, Object>> rawData = progressStatusService.getChartData(userid);
            System.out.println("쿼리 결과 데이터: " + rawData);

            // 데이터 가공
            List<String> labels = new ArrayList<>();
            List<Integer> data = new ArrayList<>();

            for (Map<String, Object> row : rawData) {
                String cltnm = (String) row.getOrDefault("cltnm", "알 수 없음");
                Integer ordflag = (Integer) row.getOrDefault("ordflag", 0);

                labels.add(cltnm);
                data.add(ordflag);
            }

            // 가공된 데이터 확인
            System.out.println("labels: " + labels);
            System.out.println("data: " + data);

            // 응답 데이터 생성
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("labels", labels);
            responseData.put("data", data);

            result.success = true;
            result.data = responseData;
            result.message = "데이터 조회 성공";

        } catch (Exception e) {
            // 예외 발생 시 로그 출력
            System.err.println("데이터 처리 중 오류: " + e.getMessage());
            e.printStackTrace();

            result.success = false;
            result.message = "데이터를 가져오는 중 오류가 발생했습니다.";
        }

        return result;
    }


}
