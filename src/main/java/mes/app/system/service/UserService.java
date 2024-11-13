package mes.app.system.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import mes.domain.entity.User;
import mes.domain.entity.UserGroup;
import mes.domain.repository.UserGroupRepository;
import mes.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.util.StringUtils;
import mes.domain.services.SqlRunner;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    SqlRunner sqlRunner;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;

    // 사용자 리스트
    public List<Map<String, Object>> getUserList(boolean superUser, String cltnm, String prenm, String biztypenm, String bizitemnm, String email) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("superUser", superUser);
        params.addValue("cltnm", cltnm);
        params.addValue("prenm", prenm);
        params.addValue("biztypenm", biztypenm);
        params.addValue("bizitemnm", bizitemnm);
        params.addValue("email", email);

        String sql = """
        SELECT
            au.id,
            up.Name,
            au.username AS  userid,
            up.UserGroup_id,
            ug.id AS group_id,
            au.email,
            au.tel,
            au.agencycd,
            ug.Name AS group_name,
            up.lang_code,
            au.is_active,
            au.Phone,
            txc.*,
            FORMAT(au.date_joined, 'yyyy-MM-dd HH:mm') AS date_joined
        FROM 
            auth_user au 
        LEFT JOIN 
            user_profile up ON up.User_id = au.id
        LEFT JOIN 
            user_group ug ON ug.id = up.UserGroup_id
        LEFT JOIN 
            TB_XCLIENT txc on	up.Name = txc.prenm
        WHERE 
            au.is_superuser = 0   
        """;

        // superUser가 아닌 경우, 개발자 그룹 제외
        if (!superUser) {
            sql += " AND ug.Code <> 'dev' ";
        }

        // 각 매개변수가 null이 아닐 경우 조건 추가
        if (!StringUtils.isEmpty(cltnm)) {
            sql += " AND uc.Value LIKE '%' + :cltnm + '%' ";
        }

        if (!StringUtils.isEmpty(prenm)) {
            sql += " AND up.Name LIKE '%' + :prenm + '%' ";
        }

        if (!StringUtils.isEmpty(biztypenm)) {
            sql += " AND rp.biztypenm LIKE '%' + :biztypenm + '%' ";
        }

        if (!StringUtils.isEmpty(bizitemnm)) {
            sql += " AND rp.bizitemnm LIKE '%' + :bizitemnm + '%' ";
        }

        if (!StringUtils.isEmpty(email)) {
            sql += " AND au.email LIKE '%' + :email + '%' ";
        }

        // 정렬 조건 추가
        sql += " ORDER BY au.date_joined DESC";

        // SQL 실행 후 결과 반환
        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, params);
        return items;
    }

    // 사용자 상세정보 조회
    public Map<String, Object> getUserDetail(Integer id){

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("id", id);

        String sql = """
			select au.id
              , up."Name"
              , au.username as login_id
              , au.email
              , ug."Name" as group_name
              , up."UserGroup_id"
              , up."Factory_id"
              , f."Name" as factory_name
              , d."Name" as dept_name
              , up."Depart_id"
              , up.lang_code
              , au.is_active
              , to_char(au.date_joined ,'yyyy-mm-dd hh24:mi') as date_joined
            from auth_user au 
            left join user_profile up on up."User_id" = au.id
            left join user_group ug on up."UserGroup_id" = ug.id 
            left join factory f on up."Factory_id" = f.id 
            left join depart d on d.id = up."Depart_id"
            where au.id = :id
		    """;

        Map<String, Object> item = this.sqlRunner.getRow(sql, dicParam);

        return item;
    }

    // 사용자 그룹 조회
    public List<Map<String, Object>> getUserGrpList(Integer id) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("id", id);

        String sql = """
        		select ug.id as grp_id
	            , ug."Name" as grp_name
	            ,rd."Char1" as grp_check
	            from user_group ug 
	            left join rela_data rd on rd."DataPk2" = ug.id 
	            and "RelationName" = 'auth_user-user_group' 
	            and rd."DataPk1" = :id
	            where coalesce(ug."Code",'') <> 'dev'
        		""";

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }

    public Boolean SaveUser(User user, Authentication auth, String authType, String authCode){

        try {
            User UserEntity = userRepository.save(user);

            List<UserGroup> AuthGroup = userGroupRepository.findByCodeAndName(authType, authCode);


            MapSqlParameterSource dicParam = new MapSqlParameterSource();


            User loginUser = (User) auth.getPrincipal();


            dicParam.addValue("loginUser", loginUser.getId());

            String sql = """
		        	INSERT INTO user_profile 
		        	("_created", "_creater_id", "User_id", "lang_code", "Name", "UserGroup_id" ) 
		        	VALUES (now(), :loginUser, :User_id, :lang_code, :name, :UserGroup_id )
		        """;

            dicParam.addValue("name", user.getFirst_name());
            dicParam.addValue("lang_code", "ko-KR");
            //dicParam.addValue("UserGroup_id", );
            dicParam.addValue("User_id", UserEntity.getId());
            dicParam.addValue("lang_code", "ko-KR");
            dicParam.addValue("UserGroup_id", AuthGroup.get(0).getId());

            this.sqlRunner.execute(sql, dicParam);
            return true;
        }catch(Exception e){
            e.getMessage();
            return false;
        }


    }

    public List<Map<String, Object>> getUserSandanList(String id) {

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("id", id);

        String sql = """
                SELECT t.userid,
                       t.spcompid AS spcompcd,
                       u_c."Value" AS spcompnm,
                       t.spplanid AS spplancd,
                       u_p."Value" AS spplannm,
                       t.spworkid AS spworkcd,
                       u_w."Value" AS spworknm,
                       t.askseq
                FROM tb_rp945 AS t
                LEFT JOIN user_code u_c ON t.spcompid = u_c.id
                LEFT JOIN user_code u_p ON t.spplanid = u_p.id
                LEFT JOIN user_code u_w ON t.spworkid = u_w.id
                WHERE t.userid = :id
                ORDER BY t.askseq;
                """;

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);
        return items;
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    public Map<String, Object> getUserDetailById(String id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        String sql = """
        select
            *
        from
            TB_XCLIENT tx 
        LEFT JOIN 
            auth_user au on tx.prenm = au.first_name
        where 
            au.id = :id;                
                """;
        return sqlRunner.getRow(sql, params);
    }

   /* public List<Map<String, Object>> searchData(String superUser, String userGroup, String keyword, String username, String departId, String divinm) {
        // 쿼리 실행 및 결과 반환
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sql = """
        SELECT au.id,
               up.[Name],
               au.username AS login_id,
               up.[UserGroup_id],
               ug.[id] AS group_id,
               au.email,
               au.tel,
               au.agencycd,
               ug.[Name] AS group_name,
               up.[Factory_id],
               uc.[Value],
               au.divinm,
               au.smtpid,
               au.smtppassword,
               rp.[ranknm],
               up.[Depart_id],
               up.lang_code,
               au.is_active,
               au.is_superuser,
               FORMAT(au.date_joined, 'yyyy-MM-dd HH:mm') AS date_joined
        FROM auth_user au
        LEFT JOIN user_profile up ON up.[User_id] = au.id
        LEFT JOIN user_group ug ON ug.id = up.[UserGroup_id]
        LEFT JOIN user_code uc ON CAST(au.agencycd AS INT) = uc.id
        LEFT JOIN tb_rp940 rp ON rp.userid = au.username
        WHERE 1=1
    """;

        // 조건부 쿼리 추가

        if (!StringUtils.isEmpty(userGroup)) {
            sql += " AND ug.[id] = :userGroup ";
            params.addValue("userGroup", userGroup);
        }

        if (!StringUtils.isEmpty(keyword)) {
            sql += " AND up.[Name] LIKE '%' + :keyword + '%' ";
            params.addValue("keyword", keyword);
        }

        if (!StringUtils.isEmpty(username)) {
            sql += " AND au.username LIKE '%' + :username + '%' ";
            params.addValue("username", username);
        }

        if (!StringUtils.isEmpty(divinm)) {
            sql += " AND rp.divinm LIKE '%' + :divinm + '%' ";
            params.addValue("divinm", divinm);
        }

        if (!StringUtils.isEmpty(departId)) {
            sql += " AND up.[Depart_id] = :departId ";
            params.addValue("departId", departId);
        }

        sql += " ORDER BY au.date_joined DESC";

        // 쿼리 실행 및 결과 반환
        return sqlRunner.getRows(sql, params);
    }*/


    public List<Map<String, Object>> searchData(String userGroup, String name, String username) {
        // 쿼리 실행 및 결과 반환
        MapSqlParameterSource params = new MapSqlParameterSource();
        String sql = """
        SELECT au.id,
               up.[Name],
               au.username AS login_id,
               up.[UserGroup_id],
               ug.[id] AS group_id,
               au.email,
               au.tel,
               au.agencycd,
               ug.[Name] AS group_name,
               up.[Factory_id],
               uc.[Value],
               au.divinm,
               au.smtpid,
               au.smtppassword,
               rp.[ranknm],
               up.[Depart_id],
               up.lang_code,
               au.is_active,
               au.is_superuser,
               FORMAT(au.date_joined, 'yyyy-MM-dd HH:mm') AS date_joined
        FROM auth_user au
        LEFT JOIN user_profile up ON up.[User_id] = au.id
        LEFT JOIN user_group ug ON ug.id = up.[UserGroup_id]
        LEFT JOIN user_code uc ON CAST(au.agencycd AS INT) = uc.id
        LEFT JOIN tb_rp940 rp ON rp.userid = au.username
        WHERE 1=1
    """;

        // 조건부 쿼리 추가

        if (!StringUtils.isEmpty(userGroup)) {
            sql += " AND ug.[id] = :userGroup ";
            params.addValue("userGroup", userGroup);
        }

        if (!StringUtils.isEmpty(name)) {
            sql += " AND up.[Name] LIKE '%' + :name + '%' ";
            params.addValue("name", name);
        }

        if (!StringUtils.isEmpty(username)) {
            sql += " AND au.username LIKE '%' + :username + '%' ";
            params.addValue("username", username);
        }

        sql += " ORDER BY au.date_joined DESC";

        // 쿼리 실행 및 결과 반환
        return sqlRunner.getRows(sql, params);
    }
}
