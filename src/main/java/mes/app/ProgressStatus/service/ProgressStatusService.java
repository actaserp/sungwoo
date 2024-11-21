package mes.app.ProgressStatus.service;

import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProgressStatusService {

    @Autowired
    SqlRunner sqlRunner;

    public List<Map<String, Object>> getProgressStatusList(String perid) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("perid", perid);

        String sql = """
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
                tb006.deldate;
            """;

        return sqlRunner.getRows(sql, params);
    }
    public List<Map<String, Object>> searchProgress(String startDate, String endDate, String remark) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startDate", startDate);
        params.addValue("endDate", endDate);
        params.addValue("endDate", endDate);

        String sql = """
        SELECT
        tb007.*, 
        tb006.*   
        FROM
            TB_DA007W tb007
        LEFT JOIN
            TB_DA006W tb006
        ON
        tb007.custcd = tb006.custcd
        AND tb007.spjangcd = tb006.spjangcd
        AND tb007.reqdate = tb006.reqdate
        AND tb007.reqnum = tb006.reqnum;
        """;

        return sqlRunner.getRows(sql, params);
    }


    public List<Map<String, Object>> getChartData(String userid) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("spjangcd", userid);

        /*String sql = """
           SELECT
                tb007.custcd,
                tb007.spjangcd,
                tb007.reqdate,
                tb007.reqnum,
                tb006.cltnm,
                MAX(CAST(tb006.ordflag AS INT)) AS ordflag
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
                tb007.custcd, tb007.spjangcd, tb007.reqdate, tb007.reqnum, tb006.cltnm
            """;*/
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


}
