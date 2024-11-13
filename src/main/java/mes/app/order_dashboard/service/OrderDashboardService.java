package mes.app.order_dashboard.service;

import mes.domain.entity.actasEntity.TB_DA006W_PK;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderDashboardService {

    @Autowired
    SqlRunner sqlRunner;

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

    //주문의뢰현황 불러오기
    public List<Map<String, Object>> getOrderList(TB_DA006W_PK tbDa006W_pk,
                                                  String searchStartDate, String searchEndDate, String searchType) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("searchStartDate", searchStartDate);
        dicParam.addValue("searchEndDate", searchEndDate);
        dicParam.addValue("searchType", searchType);

        StringBuilder sql = new StringBuilder("""
                SELECT
                    custcd,
                    spjangcd,
                    reqnum,
                    reqdate,
                    ordflag,
                    deldate,
                    telno,
                    perid,
                    cltzipcd,
                    cltaddr,
                    remark
                FROM
                    TB_DA006W hd
                WHERE
                    hd.custcd = :custcd
                    AND hd.spjangcd = :spjangcd
                """);
        // 날짜 필터
        if (searchStartDate != null && !searchStartDate.isEmpty()) {
            sql.append(" AND reqdate >= :searchStartDate");
        }
        //
        if (searchEndDate != null && !searchEndDate.isEmpty()) {
            sql.append(" AND reqdate <= :searchEndDate");
        }
        // 진행구분 필터
        if (searchType != null && !searchType.isEmpty()) {
            sql.append(" AND ordflag LIKE :searchType");
        }
        // 정렬 조건 추가
        sql.append(" ORDER BY reqdate ASC");

        dicParam.addValue("custcd", tbDa006W_pk.getCustcd());
        dicParam.addValue("spjangcd", tbDa006W_pk.getSpjangcd());
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }

    public List<Map<String, Object>> initDatas(TB_DA006W_PK tbDa006WPk) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        StringBuilder sql = new StringBuilder("""
                SELECT
                    hd.ordflag,
                    COUNT(*) AS ordflag_count
                FROM
                    TB_DA006W hd
                WHERE
                    hd.custcd = :custcd
                    AND hd.spjangcd = :spjangcd
                GROUP BY
                    hd.ordflag;
                """);
        dicParam.addValue("custcd", tbDa006WPk.getCustcd());
        dicParam.addValue("spjangcd", tbDa006WPk.getSpjangcd());
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }
}
