package mes.app.system.service;

import java.util.List;
import java.util.Map;

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

    // 사용자 리스트 조회
    /*public List<Map<String, Object>> getUserList(boolean superUser, Integer group, String keyword, String username, Integer departId, String divinm){

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("group", group);
        dicParam.addValue("keyword", keyword);
        dicParam.addValue("username", username);
        dicParam.addValue("departId", departId);
        dicParam.addValue("divinm", divinm);


        String sql = """
			select au.id
              , up."Name"
              , au.username as login_id
              , up."UserGroup_id"
              , ug."id" as group_id
              , au.email
              , au.tel
              , au.agencycd
              , ug."Name" as group_name
              , up."Factory_id"
              , uc."Value"
			  , au.divinm
			  , rp."ranknm"
              , up."Depart_id"
              , up.lang_code
              , au.is_active
              , to_char(au.date_joined ,'yyyy-mm-dd hh24:mi') as date_joined
            from auth_user au 
            left join user_profile up on up."User_id" = au.id
            left join user_group ug on ug.id = up."UserGroup_id"
            left join user_code uc on au.agencycd::Integer = uc.id
			left join tb_rp940 rp on rp.userid = au.username
            where is_superuser = false
		    """;

        if (superUser != true) {
            sql += "  and ug.\"Code\" <> 'dev' ";
        }

        if (group!=null){
            sql+= " and ug.\"id\" = :group ";
        }

        if (!StringUtils.isEmpty(keyword)) {
            sql += " and up.\"Name\" like concat('%%', :keyword, '%%') ";
        }

        if (!StringUtils.isEmpty(username)) {
            sql += " and au.\"username\" like concat('%%', :username, '%%') ";
        }
        if (!StringUtils.isEmpty(divinm)) {
            sql += " and rp.\"divinm\" like concat('%%', :divinm, '%%') ";
        }
        if (departId != null) {
            sql += " and up.\"Depart_id\" = :departId ";
        }

        sql += "order by au.date_joined desc";


        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

        return items;
    }*/

    // 사용자 리스트 조회
    public List<Map<String, Object>> getUserList(boolean superUser, Integer group, String keyword, String username, Integer departId, String divinm){

        MapSqlParameterSource dicParam = new MapSqlParameterSource();
        dicParam.addValue("group", group);
        dicParam.addValue("keyword", keyword);
        dicParam.addValue("username", username);
        dicParam.addValue("departId", departId);
        dicParam.addValue("divinm", divinm);

        String sql = """
        SELECT 
            au.id,
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
            rp.[ranknm],
            up.[Depart_id],
            up.lang_code,
            au.is_active,
            FORMAT(au.date_joined, 'yyyy-MM-dd HH:mm') AS date_joined
        FROM 
            auth_user au 
        LEFT JOIN 
            user_profile up ON up.[User_id] = au.id
        LEFT JOIN 
            user_group ug ON ug.id = up.[UserGroup_id]
        LEFT JOIN 
            user_code uc ON CAST(au.agencycd AS INT) = uc.id
        LEFT JOIN 
            tb_rp940 rp ON rp.userid = au.username
        WHERE 
            au.is_superuser = 0
        """;

        if (!superUser) {
            sql += " AND ug.[Code] <> 'dev' ";
        }

        if (group != null) {
            sql += " AND ug.[id] = :group ";
        }

        if (!StringUtils.isEmpty(keyword)) {
            sql += " AND up.[Name] LIKE '%' + :keyword + '%' ";
        }

        if (!StringUtils.isEmpty(username)) {
            sql += " AND au.[username] LIKE '%' + :username + '%' ";
        }

        if (!StringUtils.isEmpty(divinm)) {
            sql += " AND rp.[divinm] LIKE '%' + :divinm + '%' ";
        }

        if (departId != null) {
            sql += " AND up.[Depart_id] = :departId ";
        }

        sql += " ORDER BY au.date_joined DESC";

        List<Map<String, Object>> items = this.sqlRunner.getRows(sql, dicParam);

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

}
