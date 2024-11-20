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

    public List<Map<String, Object>> LastYearCnt() {

        String sql = """
                   DECLARE @Today DATE = CAST(GETDATE() AS DATE);
                DECLARE @LastYearStart DATE = DATEADD(YEAR, -1, CAST(CONCAT(YEAR(@Today), '-01-01') AS DATE));
                DECLARE @LastYearEnd DATE = @Today;
                DECLARE @ThisYearStart DATE = CAST(CONCAT(YEAR(@Today), '-01-01') AS DATE);
                DECLARE @ThisYearEnd DATE = @Today;
                
                -- 작년 1월 1일부터 작년 오늘 날짜까지의 행 개수
                SELECT COUNT(*) AS LastYearCount
                FROM ERP_SWSPANEL1.dbo.TB_DA006W
                WHERE CAST(reqdate AS DATE) BETWEEN @LastYearStart AND @LastYearEnd;
                
                -- 올해 1월 1일부터 오늘 날짜까지의 행 개수
                SELECT COUNT(*) AS ThisYearCount
                FROM ERP_SWSPANEL1.dbo.TB_DA006W
                WHERE CAST(reqdate AS DATE) BETWEEN @ThisYearStart AND @ThisYearEnd;
					""";

        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, null);

        return items;
    }

    public List<Map<String, Object>> ThisYearCnt() {

        String sql = """
                   DECLARE @Today DATE = CAST(GETDATE() AS DATE);
                DECLARE @LastYearStart DATE = DATEADD(YEAR, -1, CAST(CONCAT(YEAR(@Today), '-01-01') AS DATE));
                DECLARE @LastYearEnd DATE = @Today;
                DECLARE @ThisYearStart DATE = CAST(CONCAT(YEAR(@Today), '-01-01') AS DATE);
                DECLARE @ThisYearEnd DATE = @Today;
                
                -- 작년 1월 1일부터 작년 오늘 날짜까지의 행 개수
                SELECT COUNT(*) AS LastYearCount
                FROM ERP_SWSPANEL1.dbo.TB_DA006W
                WHERE CAST(reqdate AS DATE) BETWEEN @LastYearStart AND @LastYearEnd;
                
                -- 올해 1월 1일부터 오늘 날짜까지의 행 개수
                SELECT COUNT(*) AS ThisYearCount
                FROM ERP_SWSPANEL1.dbo.TB_DA006W
                WHERE CAST(reqdate AS DATE) BETWEEN @ThisYearStart AND @ThisYearEnd;
					""";

        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, null);

        return items;
    }

    public List<Map<String, Object>> ThisYearCntOfMonth() {

        String sql = """
                   DECLARE @Today DATE = CAST(GETDATE() AS DATE);
                              DECLARE @LastYearStart DATE = DATEADD(YEAR, -1, CAST(CONCAT(YEAR(@Today), '-01-01') AS DATE));
                              DECLARE @ThisYearStart DATE = CAST(CONCAT(YEAR(@Today), '-01-01') AS DATE);
                
                              -- 작년 월별 행 개수
                              SELECT\s
                                  MONTH(CAST(reqdate AS DATE)) AS Month,
                                  COUNT(*) AS LastYearCount
                              FROM\s
                                  ERP_SWSPANEL1.dbo.TB_DA006W
                              WHERE\s
                                  CAST(reqdate AS DATE) BETWEEN @LastYearStart AND @Today
                              GROUP BY\s
                                  MONTH(CAST(reqdate AS DATE))
                              ORDER BY\s
                                  Month;
                
                              -- 올해 월별 행 개수
                              SELECT\s
                                  MONTH(CAST(reqdate AS DATE)) AS Month,
                                  COUNT(*) AS ThisYearCount
                              FROM\s
                                  ERP_SWSPANEL1.dbo.TB_DA006W
                              WHERE\s
                                  CAST(reqdate AS DATE) BETWEEN @ThisYearStart AND @Today
                              GROUP BY\s
                                  MONTH(CAST(reqdate AS DATE))
                              ORDER BY\s
                                  Month;
					""";

        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, null);

        return items;
    }

    public List<Map<String, Object>> LastYearCntOfMonth() {

        String sql = """
                   DECLARE @Today DATE = CAST(GETDATE() AS DATE);
                              DECLARE @LastYearStart DATE = DATEADD(YEAR, -1, CAST(CONCAT(YEAR(@Today), '-01-01') AS DATE));
                              DECLARE @ThisYearStart DATE = CAST(CONCAT(YEAR(@Today), '-01-01') AS DATE);
                
                              -- 작년 월별 행 개수
                              SELECT\s
                                  MONTH(CAST(reqdate AS DATE)) AS Month,
                                  COUNT(*) AS LastYearCount
                              FROM\s
                                  ERP_SWSPANEL1.dbo.TB_DA006W
                              WHERE\s
                                  CAST(reqdate AS DATE) BETWEEN @LastYearStart AND @Today
                              GROUP BY\s
                                  MONTH(CAST(reqdate AS DATE))
                              ORDER BY\s
                                  Month;
                
                              -- 올해 월별 행 개수
                              SELECT\s
                                  MONTH(CAST(reqdate AS DATE)) AS Month,
                                  COUNT(*) AS ThisYearCount
                              FROM\s
                                  ERP_SWSPANEL1.dbo.TB_DA006W
                              WHERE\s
                                  CAST(reqdate AS DATE) BETWEEN @ThisYearStart AND @Today
                              GROUP BY\s
                                  MONTH(CAST(reqdate AS DATE))
                              ORDER BY\s
                                  Month;
					""";

        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, null);

        return items;
    }

    public List<Map<String, Object>> ThisMonthCntOfDate() {

        String sql = """
                   SELECT
                       hd.reqdate,
                       hd.ordflag,
                       COUNT(*) AS ordflag_count
                   FROM
                       TB_DA006W hd
                   WHERE
                       hd.custcd = @custcd
                       AND hd.spjangcd = @spjangcd
                       AND hd.reqdate BETWEEN FORMAT(DATEFROMPARTS(YEAR(GETDATE()), MONTH(GETDATE()), 1), 'yyyyMMdd')
                                           AND FORMAT(GETDATE(), 'yyyyMMdd')
                   GROUP BY
                       hd.reqdate,
                       hd.ordflag
                   ORDER BY
                       hd.reqdate;
					""";

        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, null);

        return items;
    }

    public List<Map<String, Object>> LastMonthCntOfDate() {

        String sql = """
            SELECT
              hd.reqdate,
              hd.ordflag,
              COUNT(*) AS ordflag_count
            FROM
              TB_DA006W hd
            WHERE
              hd.custcd = :custcd
              AND hd.spjangcd = :spjangcd
              AND hd.reqdate BETWEEN FORMAT(DATEFROMPARTS(YEAR(DATEADD(MONTH, -1, GETDATE())), MONTH(DATEADD(MONTH, -1, GETDATE())), 1), 'yyyyMMdd')
                                  AND FORMAT(EOMONTH(DATEADD(MONTH, -1, GETDATE())), 'yyyyMMdd')
            GROUP BY
              hd.reqdate,
              hd.ordflag
            ORDER BY
              hd.reqdate;
            """;

        List<Map<String,Object>> items = this.sqlRunner.getRows(sql, null);

        return items;
    }
}
