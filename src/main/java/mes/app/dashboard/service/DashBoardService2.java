package mes.app.dashboard.service;

import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DashBoardService2 {
    @Autowired
    SqlRunner sqlRunner;

    // 사용자의 사업장코드 return
    public String getSpjangcd(String username
                            , String searchSpjangcd) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql = """
                SELECT spjangcd
                FROM auth_user
                WHERE username = :username
                """;
        dicParam.addValue("username", username);
        Map<String, Object> spjangcdMap = this.sqlRunner.getRow(sql, dicParam);
        String userSpjangcd = (String)spjangcdMap.get("spjangcd");

        String spjangcd = searchSpjangcd(searchSpjangcd, userSpjangcd);
        return spjangcd;
    }
    // init에 필요한 사업장코드 반환
    public String searchSpjangcd(String searchSpjangcd, String userSpjangcd){

        String resultSpjangcd = "";
        switch (searchSpjangcd){
            case "ZZ":
                resultSpjangcd = searchSpjangcd;
                break;
                case "PP":
                    resultSpjangcd= searchSpjangcd;
                    break;
                    default:
                        resultSpjangcd = userSpjangcd;
        }
        return resultSpjangcd;
    }

    // username으로 cltcd, cltnm, saupnum, custcd 가지고 오기
    public Map<String, Object> getUserInfo(String username) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql = """
                select custcd,
                        cltcd,
                        cltnm
                FROM TB_XCLIENT
                WHERE saupnum = :username
                """;
        dicParam.addValue("username", username);
        Map<String, Object> userInfo = this.sqlRunner.getRow(sql, dicParam);
        return userInfo;
    }

    // 작년 진행구분(ordflag)별 데이터 개수
    public List<Map<String, Object>> LastYearCnt(String spjangcd) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql = """
            WITH DateRanges AS (
               SELECT
                   CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + '0101' AS PrevYearStart, -- 전년도 1월 1일
                   CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS PrevYearEnd, -- 전년도 오늘 날짜
                   CAST(YEAR(GETDATE()) AS CHAR(4)) + '0101' AS ThisYearStart, -- 올해 1월 1일
                   CAST(YEAR(GETDATE()) AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS ThisYearEnd, -- 올해 오늘 날짜
                   CAST(YEAR(GETDATE()) AS CHAR(4)) + LEFT(CONVERT(VARCHAR(8), GETDATE(), 112), 6) + '01' AS ThisMonthStart, -- 올해 당월 1일
                   CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + LEFT(CONVERT(VARCHAR(8), GETDATE(), 112), 6) + '01' AS LastYearThisMonthStart -- 작년 당월 1일
            )
            SELECT
                   ordflag,
                   COUNT(*) AS TotalCount
               FROM TB_DA006W
               CROSS JOIN DateRanges
               WHERE
                   LEN(reqdate) = 8 AND                        -- 8자리 문자열인지 확인
                   reqdate LIKE '[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]' AND -- YYYYMMDD 형식인지 확인
                   CONVERT(DATE, reqdate, 112) BETWEEN CONVERT(DATE, PrevYearStart, 112) AND CONVERT(DATE, PrevYearEnd, 112)
                   AND spjangcd = :spjangcd
               GROUP BY ordflag
            """;
        dicParam.addValue("spjangcd", spjangcd);
        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }
    // 올해 진행구분(ordflag)별 데이터 개수
    public List<Map<String, Object>> ThisYearCnt(String spjangcd) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql = """
            WITH DateRanges AS (
               SELECT
                   CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + '0101' AS PrevYearStart, -- 전년도 1월 1일
                   CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS PrevYearEnd, -- 전년도 오늘 날짜
                   CAST(YEAR(GETDATE()) AS CHAR(4)) + '0101' AS ThisYearStart, -- 올해 1월 1일
                   CAST(YEAR(GETDATE()) AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS ThisYearEnd, -- 올해 오늘 날짜
                   CAST(YEAR(GETDATE()) AS CHAR(4)) + LEFT(CONVERT(VARCHAR(8), GETDATE(), 112), 6) + '01' AS ThisMonthStart, -- 올해 당월 1일
                   CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + LEFT(CONVERT(VARCHAR(8), GETDATE(), 112), 6) + '01' AS LastYearThisMonthStart -- 작년 당월 1일
            )
            SELECT
                   ordflag,
                   COUNT(*) AS TotalCount
               FROM TB_DA006W
               CROSS JOIN DateRanges
               WHERE
                   LEN(reqdate) = 8 AND                        -- 8자리 문자열인지 확인
                   reqdate LIKE '[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]' AND -- YYYYMMDD 형식인지 확인
                   CONVERT(DATE, reqdate, 112) BETWEEN CONVERT(DATE, ThisYearStart, 112) AND CONVERT(DATE, ThisYearEnd, 112)
                   AND spjangcd = :spjangcd
               GROUP BY ordflag
            """;
        dicParam.addValue("spjangcd", spjangcd);
        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }

    // 올해 월별 데이터
    public List<Map<String, Object>> ThisYearCntOfMonth(String spjangcd) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql = """
                WITH DateRanges AS (
                    SELECT
                        CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + '0101' AS PrevYearStart,
                        CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS PrevYearEnd,
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + '0101' AS ThisYearStart,
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS ThisYearEnd,
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + LEFT(CONVERT(VARCHAR(8), GETDATE(), 112), 6) + '01' AS ThisMonthStart,
                        CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + LEFT(CONVERT(VARCHAR(8), GETDATE(), 112), 6) + '01' AS LastYearThisMonthStart
                ),
                MonthGenerator AS (
                    -- 올해 1월부터 12월까지 월 목록 생성
                    SELECT FORMAT(DATEADD(MONTH, number, ThisYearStart), 'yyyy-MM') AS Month
                    FROM master.dbo.spt_values
                    CROSS JOIN DateRanges
                    WHERE type = 'P' AND number BETWEEN 0 AND 11
                )
                SELECT
                    mg.Month,
                    ISNULL(COUNT(td.reqdate), 0) AS TotalCount
                FROM MonthGenerator mg
                LEFT JOIN TB_DA006W td
                    ON FORMAT(CONVERT(DATE, td.reqdate, 112), 'yyyy-MM') = mg.Month
                    AND td.spjangcd = :spjangcd
                    AND LEN(td.reqdate) = 8
                    AND td.reqdate LIKE '[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]'
                GROUP BY mg.Month
                ORDER BY mg.Month
           """;
        dicParam.addValue("spjangcd", spjangcd);
        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }
    // 작년 월별 데이터
    public List<Map<String, Object>> LastYearCntOfMonth(String spjangcd) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql = """
                WITH DateRanges AS (
                    SELECT
                        CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + '0101' AS PrevYearStart,
                        CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + '1231' AS PrevYearEnd,
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + '0101' AS ThisYearStart,
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS ThisYearEnd,
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + LEFT(CONVERT(VARCHAR(8), GETDATE(), 112), 6) + '01' AS ThisMonthStart,
                        CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + LEFT(CONVERT(VARCHAR(8), GETDATE(), 112), 6) + '01' AS LastYearThisMonthStart
                ),
                MonthGenerator AS (
                    -- 작년 1월부터 12월까지 월 목록 생성
                    SELECT FORMAT(DATEADD(MONTH, number, PrevYearStart), 'yyyy-MM') AS Month
                    FROM master.dbo.spt_values
                    CROSS JOIN DateRanges
                    WHERE type = 'P' AND number BETWEEN 0 AND 11
                )
                SELECT
                    mg.Month,
                    ISNULL(COUNT(td.reqdate), 0) AS TotalCount
                FROM MonthGenerator mg
                LEFT JOIN TB_DA006W td
                    ON FORMAT(CONVERT(DATE, td.reqdate, 112), 'yyyy-MM') = mg.Month
                    AND td.spjangcd = :spjangcd
                    AND LEN(td.reqdate) = 8
                    AND td.reqdate LIKE '[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]'
                GROUP BY mg.Month
                ORDER BY mg.Month
           """;
        dicParam.addValue("spjangcd", spjangcd);
        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }
    // 올해 이번달 일별 데이터 개수
    public List<Map<String, Object>> ThisMonthCntOfDate(String spjangcd) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql = """
                WITH DateRanges AS (
                    SELECT
                        CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + '0101' AS PrevYearStart, -- 전년도 1월 1일
                        CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS PrevYearEnd, -- 전년도 오늘 날짜
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + '0101' AS ThisYearStart, -- 올해 1월 1일
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS ThisYearEnd, -- 올해 오늘 날짜
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + RIGHT('0' + CAST(MONTH(GETDATE()) AS VARCHAR(2)), 2) + '01' AS ThisMonthStart, -- 올해 당월 1일
                        EOMONTH(GETDATE()) AS ThisMonthEnd -- 이번 달 마지막 날
                ),
                DateGenerator AS (
                    -- 이번 달 1일부터 말일까지 날짜 생성
                    SELECT DATEADD(DAY, number, CONVERT(DATE, ThisMonthStart, 112)) AS Day
                    FROM master.dbo.spt_values
                    CROSS JOIN DateRanges
                    WHERE type = 'P'
                    AND DATEADD(DAY, number, CONVERT(DATE, ThisMonthStart, 112)) <= ThisMonthEnd
                )
                SELECT
                    dg.Day,
                    ISNULL(COUNT(td.reqdate), 0) AS TotalCount
                FROM DateGenerator dg
                LEFT JOIN TB_DA006W td
                    ON CONVERT(DATE, td.reqdate, 112) = dg.Day
                    AND td.spjangcd = :spjangcd
                    AND LEN(td.reqdate) = 8
                    AND td.reqdate LIKE '[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]'
                GROUP BY dg.Day
                ORDER BY dg.Day;
            """;
        dicParam.addValue("spjangcd", spjangcd);
        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }
    // 전월 일별 데이터 개수
    public List<Map<String, Object>> LastMonthCntOfDate(String spjangcd) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        String sql = """
                WITH DateRanges AS (
                    SELECT
                        CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + '0101' AS PrevYearStart, -- 전년도 1월 1일
                        CAST(YEAR(GETDATE()) - 1 AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS PrevYearEnd, -- 전년도 오늘 날짜
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + '0101' AS ThisYearStart, -- 올해 1월 1일
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + RIGHT(CONVERT(VARCHAR(8), GETDATE(), 112), 4) AS ThisYearEnd, -- 올해 오늘 날짜
                        CAST(YEAR(GETDATE()) AS CHAR(4)) + RIGHT('0' + CAST(MONTH(GETDATE()) AS VARCHAR(2)), 2) + '01' AS ThisMonthStart, -- 올해 당월 1일
                        EOMONTH(GETDATE()) AS ThisMonthEnd, -- 이번 달 마지막 날
                        FORMAT(DATEADD(MONTH, -1, GETDATE()), 'yyyyMM') + '01' AS PrevMonthStart, -- 전월 1일
                        EOMONTH(DATEADD(MONTH, -1, GETDATE())) AS PrevMonthEnd -- 전월 마지막 날
                ),
                DateGenerator AS (
                    -- 전월 1일부터 전월 말일까지 모든 날짜 생성
                    SELECT DATEADD(DAY, number, CONVERT(DATE, PrevMonthStart, 112)) AS Day
                    FROM master.dbo.spt_values
                    CROSS JOIN DateRanges
                    WHERE type = 'P'
                    AND DATEADD(DAY, number, CONVERT(DATE, PrevMonthStart, 112)) <= CONVERT(DATE, PrevMonthEnd, 112)
                )
                SELECT
                    dg.Day,
                    ISNULL(COUNT(td.reqdate), 0) AS TotalCount
                FROM DateGenerator dg
                LEFT JOIN TB_DA006W td
                    ON CONVERT(DATE, td.reqdate, 112) = dg.Day
                    AND td.spjangcd = :spjangcd
                    AND LEN(td.reqdate) = 8
                    AND td.reqdate LIKE '[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]'
                GROUP BY dg.Day
                ORDER BY dg.Day;
            """;
        dicParam.addValue("spjangcd", spjangcd);
        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }
}
