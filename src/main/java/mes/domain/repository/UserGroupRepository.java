package mes.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mes.domain.entity.UserGroup;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {

	List<UserGroup> findByName(String name);
	
	UserGroup getUserGrpById(Integer id);

	List<UserGroup> findByCodeAndName(String code, String name);

    UserGroup findByCode(String code);
}
