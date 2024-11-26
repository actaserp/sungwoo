package mes.app.ProgressStatus.service;

import lombok.extern.slf4j.Slf4j;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProgressStatusService {

    @Autowired
    SqlRunner sqlRunner;

    public List<Map<String, Object>> getProgressStatusList(String perid, String spjangcd) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("perid", perid);
        params.addValue("spjangcd", spjangcd);

        /*String sql = """
            SELECT
                tb007.custcd,         -- 고객 코드
                tb007.spjangcd,       -- 사업장 코드
                tb007.reqdate,        -- 주문일자
                tb007.reqnum,         -- 주문번호
                tb007.reqseq,         -- 주문 순번
                tb006.cltnm,          -- 업체명
                tb006.perid,          -- 담당자
                tb006.telno,          -- 연락처
                tb006.ordflag,        -- 진행구분
                tb006.remark,         -- 비고
                tb006.deldate        -- 출고일자
            FROM
                TB_DA007W tb007
            LEFT JOIN
                TB_DA006W tb006
            ON
                tb007.custcd = tb006.custcd
                AND tb007.spjangcd = tb006.spjangcd
                AND tb007.reqdate = tb006.reqdate
                AND tb007.reqnum = tb006.reqnum
            GROUP BY
                tb007.custcd,
                tb007.spjangcd,
                tb007.reqdate,
                tb007.reqnum,
                tb007.reqseq,
                tb006.cltnm,
                tb006.perid,
                tb006.telno,
                tb006.ordflag,
                tb006.remark,
                tb006.deldate
            ORDER BY
                tb007.reqdate DESC;
            """;*/
        String sql = """
                 SELECT
            tb006.*
        FROM
            TB_DA006W tb006
        WHERE
            1=1
        ORDER BY
            reqdate DESC;
            """;

        return sqlRunner.getRows(sql, params);
    }


    public List<Map<String, Object>> getChartData2(
            String userid, String search_spjangcd, String startDate, String endDate,
            String searchCltnm, Integer searchtketnm, String searchTitle) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder("""
        SELECT cltnm, ordflag
        FROM tb_da006w
        WHERE 1=1
    """);

        // 관리자(super)일 때는 saupnum 조건 제외
        if (userid != null && !userid.isEmpty()) {
            // userid가 "super" 또는 "seong"이 아닌 경우에만 조건 추가
            if (!"super".equals(userid) && !"seong".equals(userid)) {
                sql.append(" AND saupnum = :saupnum");
                params.addValue("saupnum", userid);
            }
        }

        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND reqdate >= :startDate");
            params.addValue("startDate", startDate);
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND reqdate <= :endDate");
            params.addValue("endDate", endDate);
        }

        if (searchCltnm != null && !searchCltnm.isEmpty()) {
            sql.append(" AND cltnm LIKE :cltnm");
            params.addValue("cltnm", "%" + searchCltnm + "%");
        }
        if (search_spjangcd != null && !search_spjangcd.isEmpty()) {
            sql.append(" AND spjangcd = :spjangcd");
            params.addValue("spjangcd", search_spjangcd);
        }

        if (searchtketnm != null) {
            sql.append(" AND ordflag = :ordflag");
            params.addValue("ordflag", searchtketnm);
        }

        if (searchTitle != null && !searchTitle.isEmpty()) {
            sql.append(" AND title LIKE :searchTitle");
            params.addValue("searchTitle", "%" + searchTitle + "%");
        }

        log.info("실행될 SQL: {}", sql.toString());
        log.info("바인딩된 파라미터: {}", params.getValues());

        return sqlRunner.getRows(sql.toString(), params);
    }

    public List<Map<String, Object>> getChartData(String userid) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("spjangcd", userid);

        String sql = """
                   SELECT
                    tb007.custcd,
                    tb007.spjangcd,
                    tb007.reqdate,
                    tb007.reqnum,
                    tb006.cltnm,
                    MAX(CAST(tb006.ordflag AS INT)) AS ordflag
                FROM
                    TB_DA007W tb007
                INNER JOIN
                    TB_DA006W tb006
                ON
                    tb007.custcd = tb006.custcd
                    AND tb007.spjangcd = tb006.spjangcd
                    AND tb007.reqdate = tb006.reqdate
                    AND tb007.reqnum = tb006.reqnum
                GROUP BY
                    tb007.custcd, tb007.spjangcd, tb007.reqdate, tb007.reqnum, tb006.cltnm;
                """;

        return sqlRunner.getRows(sql, params);
    }

    public List<Map<String, Object>> searchProgressStatus(
            String startDate, String endDate,
            String searchTitle, String searchtketnm,
            String searchCltnm, String userid, String spjangcd,List<String> groupByColumns
            ) {

        // 동적 쿼리를 위한 매개변수 설정
        MapSqlParameterSource params = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder("""
        SELECT
            tb006.*
        FROM
            TB_DA006W tb006
        WHERE
            1=1
        ORDER BY
            reqdate DESC;
        """); // WHERE 1=1은 조건을 추가하기 쉽게 하기 위한 트릭입니다.

        // 조건 추가
        if (startDate != null && !startDate.isEmpty()) {
            sql.append(" AND tb006.reqdate >= :startDate");
            params.addValue("startDate", startDate);
        }

        if (endDate != null && !endDate.isEmpty()) {
            sql.append(" AND tb006.reqdate <= :endDate");
            params.addValue("endDate", endDate);
        }

        if (searchCltnm != null && !searchCltnm.isEmpty()) {
            sql.append(" AND tb006.cltnm LIKE :cltnm");
            params.addValue("cltnm", "%" + searchCltnm + "%");
        }

        if (searchtketnm != null && !searchtketnm.isEmpty()) {
            sql.append(" AND tb006.ordflag = :ordflag");
            params.addValue("ordflag", searchtketnm);
        }

        if (searchTitle != null && !searchTitle.isEmpty()) {
            sql.append(" AND tb006.remark LIKE :searchRemark");
            params.addValue("searchRemark", "%" + searchTitle + "%");
        }

        // Optional: spjangcd 필터링 추가
        if (spjangcd != null && !spjangcd.isEmpty()) {
            sql.append(" AND tb006.spjangcd = :spjangcd");
            params.addValue("spjangcd", spjangcd);
        }

        // 관리자(super)일 때는 saupnum 조건 제외
        if (userid != null && !userid.isEmpty()) {
            // userid가 "super" 또는 "seong"이 아닌 경우에만 조건 추가
            if (!"super".equals(userid) && !"seong".equals(userid)) {
                sql.append(" AND tb006.saupnum = :saupnum");
                params.addValue("saupnum", userid);
            }
        }

        // 로그 출력
/*        log.info("검색 실행될 SQL: {}", sql.toString());
        log.info("검색 바인딩된 파라미터: {}", params.getValues());*/

        // 데이터 조회
        return sqlRunner.getRows(sql.toString(), params);
    }

}
