package mes.app.account.service;

import mes.domain.entity.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mes.domain.services.SqlRunner;


@Service
public class UserProfileService {
    @Autowired
    private SqlRunner sqlRunner;


    @Transactional
    public void saveUserProfile(UserProfile userProfile) {
        String sql = "INSERT INTO user_profile (user_id, name) VALUES (:userId, :name)"; // 파라미터 이름 사용

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", userProfile.getUser().getId());
        params.addValue("name", userProfile.getName());

        // SQL 쿼리 실행
        sqlRunner.execute(sql, params); // execute 메서드를 사용하여 INSERT 실행
    }
}
