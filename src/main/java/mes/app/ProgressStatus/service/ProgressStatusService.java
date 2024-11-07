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
}
