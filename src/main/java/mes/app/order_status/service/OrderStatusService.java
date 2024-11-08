package mes.app.order_status.service;

import mes.domain.entity.actasEntity.TB_DA006W;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderStatusService {

    @Autowired
    SqlRunner sqlRunner;

    public List<Map<String, Object>> getOrderStatusByOperid(String perid) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("perid", perid);

        String sql = """
        SELECT
            tb007.*,
            tb007.hgrb,
            tb007.qty,
            tb007.panel_t,
            tb007.panel_w,
            tb007.panel_l,
            tb007.exfmtypedv,
            tb007.infmtypedv,
            tb007.stframedv,
            tb007.stexplydv,
            tb007.ordtext,
            tb006.*,
            tb006.ordflag,
            tb006.reqdate,
            tb006.cltnm,
            tb006.remark,
            tb006.deldate
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

    public List<Map<String, Object>> getModalListByClientName(String searchTerm) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        // searchTerm이 있을 때만 LIKE 조건 추가
        String sql = """
        SELECT *
                FROM ERP_SWSPANEL1.dbo.TB_XCLIENT
        """ + (searchTerm != null && !searchTerm.isEmpty() ? " WHERE cltnm LIKE :searchTerm" : "");

        // searchTerm이 비어 있지 않을 때만 파라미터에 추가
        if (searchTerm != null && !searchTerm.isEmpty()) {
            params.addValue("searchTerm", "%" + searchTerm + "%");
        }
        return sqlRunner.getRows(sql, params);
    }

    public List<Map<String, Object>> searchData(String startDate, String endDate, String searchCltnm, String searchtketnm, String searchstate) {

        MapSqlParameterSource params = new MapSqlParameterSource();
        if (startDate != null && !startDate.isEmpty()) {
            params.addValue("startDate", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            params.addValue("endDate", endDate);
        }
        if (searchCltnm != null && !searchCltnm.isEmpty()) {
            params.addValue("searchCltnm", "%" + searchCltnm + "%");
        }
        if (searchtketnm != null && !searchtketnm.isEmpty()) {
            params.addValue("searchtketnm", "%" + searchtketnm + "%");
        }
        if (searchstate != null && !searchstate.isEmpty() && !searchstate.equals("전체")) {
            params.addValue("searchstate", searchstate);
        }

        // 기본 SQL 쿼리 작성
        String sql = """
        SELECT
            tb007.*,
            tb007.reqdate,
            tb006.*,
            tb006.cltnm,
            tb006.remark,
            tb006.ordflag
        FROM
            TB_DA007W tb007
        LEFT JOIN
            TB_DA006W tb006
        ON
            tb007.custcd = tb006.custcd
            AND tb007.spjangcd = tb006.spjangcd
            AND tb007.reqdate = tb006.reqdate
            AND tb007.reqnum = tb006.reqnum
        WHERE 1=1
    """;

        // 조건 추가
        if (params.hasValue("startDate")) {
            sql += " AND tb007.reqdate >= :startDate";
        }
        if (params.hasValue("endDate")) {
            sql += " AND tb007.reqdate <= :endDate";
        }
        if (params.hasValue("searchCltnm")) {
            sql += " AND tb006.cltnm LIKE :searchCltnm";
        }
        if (params.hasValue("searchtketnm")) {
            sql += " AND tb006.remark LIKE :searchtketnm";
        }
        if (params.hasValue("searchstate")) {
            sql += " AND tb006.ordflag = :searchstate";
        }

        // 쿼리 실행 및 결과 반환
        return sqlRunner.getRows(sql, params);
    }
}