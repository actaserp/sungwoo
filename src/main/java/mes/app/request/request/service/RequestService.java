package mes.app.request.request.service;

import mes.domain.entity.actasEntity.TB_DA006W;
import mes.domain.entity.actasEntity.TB_DA006W_PK;
import mes.domain.entity.actasEntity.TB_DA007W;
import mes.domain.entity.actasEntity.TB_RP920;
import mes.domain.repository.TB_RP920Repository;
import mes.domain.repository.actasRepository.TB_DA006WRepository;
import mes.domain.repository.actasRepository.TB_DA007WRepository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

@Service
public class RequestService {

    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    TB_DA006WRepository tbDa006WRepository;

    @Autowired
    TB_DA007WRepository tbDa007WRepository;
    // 헤드정보 저장
    @Transactional
    public Boolean save(TB_DA006W tbDa006W){

        try{
            tbDa006WRepository.save(tbDa006W);
            return true;
        }catch (Exception e){
            System.out.println(e + ": 에러발생");
            return false;
        }
    }
    // 세부항목 저장
    @Transactional
    public Boolean saveBody(TB_DA007W tbDa007W){

        try{
            tbDa007WRepository.save(tbDa007W);

            return true;

        }catch (Exception e){
            System.out.println(e + ": 에러발생");
            return false;
        }
    }
    //세부항목 불러오기
    public List<Map<String, Object>> getInspecList(TB_DA006W_PK tbDa006W_pk) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql = """
                select 
                *
                from TB_DA007W
                WHERE reqnum = :reqnum
                AND   custcd = :custcd
                AND   spjangcd = :spjangcd
                order by indate desc
                """;
        dicParam.addValue("reqnum", tbDa006W_pk.getReqnum());
        dicParam.addValue("custcd", tbDa006W_pk.getCustcd());
        dicParam.addValue("spjangcd", tbDa006W_pk.getSpjangcd());
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }
    //주문의뢰현황 불러오기
    public List<Map<String, Object>> getOrderList() {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql = """
                select 
                *
                from TB_DA007W
                order by indate desc
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }
    // 세부항목 업데이트
    public boolean updateBody(TB_DA007W tbDa007W) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        try {
            String sql = """
                    update set
                    """;
            dicParam.addValue("spworknm", tbDa007W.getPk().getCustcd());

            this.sqlRunner.execute(sql, dicParam);
        }catch(Exception e){
            e.getMessage();
            e.printStackTrace();
            return false;
        }
        return true;
    }
    // file download
    public List<Map<String, Object>> download(Map<String, Object> reqnum) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        StringBuilder sql = new StringBuilder();
        dicParam.addValue("uniqcode", reqnum.get("uniqcode"));

        sql.append("""
                select 
                        filepath,
                        filesvnm,
                        fileornm
                from tb_
                where 
                    uniqcode = :uniqcode
                """);
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql.toString(), dicParam);
        return items;
    }
    // 제품구성 리스트 불러오는 함수
    public List<Map<String, Object>> getListHgrb() {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql = """
                select 
                TB_CA503_07.hgrb,
                TB_CA503_07.hgrbnm,
                TB_CA503_07.sortno
                from TB_CA503_07 WITH(NOLOCK)
                WHERE TB_CA503_07.custcd = 'SWSPANEL'
                 AND TB_CA503_07.grb = '1'
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }
    // 보강재, 마감재 리스트 불러오는 함수
    public List<Map<String, Object>> getListCgrb() {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql = """
                SELECT Z.cgrb   cgrb,
                       Z.cgrbnm cgrbnm,
                       Z.sortno sortno
                  FROM (SELECT agrb + bgrb + cgrb cgrb,
                               cgrbnm cgrbnm,
                               sortno sortno
                          FROM TB_CA503_C WITH(NOLOCK)
                         WHERE (custcd = 'SWSPANEL')
                 and agrb = 'M'
                         UNION ALL
                        SELECT 'Z' + 'Z' + hgrb cgrb,
                               hgrbnm cgrbnm,
                               sortno sortno
                          FROM TB_CA503_07 WITH(NOLOCK)
                         WHERE (custcd = 'SWSPANEL')
                           AND (grb    = '3')) Z
                
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
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
    // username으로 주무의뢰 필요 데이터 가져오기
    public Map<String, Object> getMyInfo(String username) {
        MapSqlParameterSource dicParam = new MapSqlParameterSource();

        String sql = """
                select x.prenm,
                       x.hptelnum,
                       x.zipcd,
                       x.cltadres
                FROM TB_XCLIENT x
                WHERE saupnum = :username
                """;
        dicParam.addValue("username", username);
        Map<String, Object> userInfo = this.sqlRunner.getRow(sql, dicParam);
        return userInfo;
    }
}
