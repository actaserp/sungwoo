package mes.app.account.service;

import mes.domain.DTO.TB_RP945Dto;
import mes.domain.entity.TB_RP945;
import mes.domain.repository.TB_RP945Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TB_RP945_Service {


    @Autowired
    TB_RP945Repository tb_rp945Repository;

    public void save(TB_RP945Dto dto){

        TB_RP945 tbRp945 = TB_RP945.toSaveEntity(dto);
        tb_rp945Repository.save(tbRp945);


    }

}
