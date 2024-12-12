package mes.app.account.service;

import mes.domain.entity.actasEntity.TB_XCLIENT;
import mes.domain.repository.actasRepository.TB_XClientRepository;
import mes.domain.services.SqlRunner;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TB_XClientService {

    @Autowired
    TB_XClientRepository tbXClientRepository;

    @Autowired
    SqlRunner sqlRunner;

    @Transactional
    public void save(TB_XCLIENT tbXClient) {
        tbXClientRepository.save(tbXClient);
    }

    public String getUserAddress(String userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("saupnum", userId);

        // 사용자 주소를 조회하는 SQL 쿼리 작성
        String sql = """
            SELECT tx.cltadres AS address
            FROM TB_XCLIENT tx
            WHERE tx.saupnum = :saupnum
            """;

        List<Map<String, Object>> result = sqlRunner.getRows(sql, params);

        // 결과가 존재하면 address 반환, 없으면 null 반환
        if (result != null && !result.isEmpty() && result.get(0).get("address") != null) {
            return result.get(0).get("address").toString();
        }
        return null;
    }

    public String getTbXa012Address() {
        // SQL 파라미터 설정
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("spjangcd1", "PP");
        params.addValue("spjangcd2", "ZZ");

        // SQL 쿼리
        String sql = """
        SELECT zipcd2, adres2a
        FROM TB_XA012
        WHERE spjangcd IN (:spjangcd1, :spjangcd2)
    """;

        // 데이터 조회
        List<Map<String, Object>> rows = sqlRunner.getRows(sql, params);
        if (rows.isEmpty()) {
            return null;
        }

        // 첫 번째 행에서 zipcd2와 adres2a 결합
        Map<String, Object> row = rows.get(0);
        String zipcd2 = (String) row.get("zipcd2");
        String adres2a = (String) row.get("adres2a");

        return adres2a;
    }

}
