package mes.app.order_status.service;

import mes.domain.entity.actasEntity.TB_DA006W;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

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
}