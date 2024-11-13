package mes.domain.repository.actasRepository;

import mes.domain.entity.actasEntity.TB_XCLIENT;
import mes.domain.entity.actasEntity.TB_XCLIENTId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TB_XClientRepository extends JpaRepository<TB_XCLIENT, TB_XCLIENTId> {

    @Query("SELECT MAX(t.id.cltcd) FROM TB_XCLIENT t")
    String findMaxCltcd();

    Optional<TB_XCLIENT> findBySaupnum(String saupnum);

}
