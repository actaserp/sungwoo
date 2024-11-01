package mes.app.account.service;

import mes.domain.entity.actasEntity.TB_XCLIENT;
import mes.domain.repository.actasRepository.TB_XClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TB_XClientService {

    @Autowired
    TB_XClientRepository tbXClientRepository;


    @Transactional
    public void save(TB_XCLIENT tbXClient) {
        tbXClientRepository.save(tbXClient);
    }
}
