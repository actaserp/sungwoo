package mes.app.ProgressStatus;


import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/api/ProgressStatus")
public class ProgressStatusController {

    @Autowired
    ProgressStatusService progressStatusService;


    @GetMapping("/read")
    public AjaxResult ProgressStatusRead(Authentication auth,  @RequestParam(value = "spjangcd", required = false) String spjangcd) {

        AjaxResult result = new AjaxResult();

        try{
            // 로그인한 사용자 정보에서 이름(perid) 가져오기
            User user = (User) auth.getPrincipal();
            String perid = user.getFirst_name();

            List<Map<String, Object>> progressStatusLis = progressStatusService.getProgressStatusList(perid, spjangcd);
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

    @GetMapping("/search")
    public AjaxResult searchProgress(@RequestParam Map<String, String> params, Authentication auth) {
        AjaxResult result = new AjaxResult();
        log.info("그리드 검색: {}", params);
        try {
            // 로그인한 사용자 정보
            User user = (User) auth.getPrincipal();
            String userid = user.getUsername();
            String spjangcd = user.getSpjangcd();

            // 검색 조건 로깅
            System.out.println(String.format("검색 조건: %s, 사용자: %s, 사업장: %s", params, userid, spjangcd));

            // 검색 서비스 호출
            List<Map<String, Object>> progressStatusList = progressStatusService.searchProgressStatus(
                    params.get("startDate"),    // 검색 시작 날짜
                    params.get("endDate"),      // 검색 종료 날짜
                    params.get("searchTitle"), // 검색 비고 ()
                    params.get("searchtketnm"),
                    params.get("searchCltnm"),
                    userid,                     // 사용자 ID
                    spjangcd                    // 사업장 코드
            );

            // 응답 생성
            result.success = true;
            result.data = progressStatusList;
            result.message = "데이터 조회 성공";
        } catch (Exception e) {
            // 예외 로그 출력
            System.out.println("검색 중 오류 발생: " + e.getMessage());
            e.printStackTrace();

            // 응답 생성
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


    //차트 데이터
    @GetMapping("/getChartData2")
    public AjaxResult getChartData(
            Authentication auth,
            @RequestParam(value = "search_spjangcd", required = false) String search_spjangcd,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "searchCltnm", required = false) String searchCltnm,
            @RequestParam(value = "searchtketnm", required = false) Integer searchtketnm,
            @RequestParam(value = "searchTitle", required = false) String searchTitle) {

        AjaxResult result = new AjaxResult();
        try {
            User user = (User) auth.getPrincipal();
            String userid = user.getUsername();

            log.info("검색 조건 -search_spjangcd:{}, startDate: {}, endDate: {}, cltnm: {}, ordflag: {}, searchTitle: {}",
                    startDate, endDate, searchCltnm, searchtketnm, searchTitle);

            List<Map<String, Object>> rawData = progressStatusService.getChartData2(
                    userid, search_spjangcd, startDate, endDate, searchCltnm, searchtketnm, searchTitle);

            if (rawData == null || rawData.isEmpty()) {
                log.warn("조회된 데이터가 없습니다.");
                result.success = false;
                result.message = "조회된 데이터가 없습니다.";
            } else {
                log.info("쿼리 결과 데이터: {}", rawData);
                result.success = true;
                result.data = rawData;
            }
        } catch (Exception e) {
            log.error("데이터 처리 중 오류: {}", e.getMessage(), e);
            result.success = false;
            result.message = "데이터를 가져오는 중 오류가 발생했습니다.";
        }

        return result;
    }


}
