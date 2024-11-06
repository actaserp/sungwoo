package mes.domain.repository.actasRepository;

import mes.domain.entity.actasEntity.TB_DA006W;
import mes.domain.entity.actasEntity.TB_DA006WFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TB_DA006WFILERepository extends JpaRepository<TB_DA006WFile,String> {
}
