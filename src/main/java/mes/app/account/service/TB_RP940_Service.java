package mes.app.account.service;

import mes.domain.DTO.TB_RP940Dto;
import mes.domain.entity.TB_RP940;
import mes.domain.repository.TB_RP940Repository;
import mes.domain.repository.TB_RP945Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TB_RP940_Service {

    @Autowired
    TB_RP940Repository tb_rp940Repository;

    @Autowired
    TB_RP945Repository tb_rp945Repository;

    public void save(TB_RP940Dto dto){

        TB_RP940 tbRp940 = TB_RP940.toSaveEntity(dto);

        tb_rp940Repository.save(tbRp940);



        /*TB_RP945 tbRp945 = TB_RP945.toSaveEntity(dto2);
        tb_rp945Repository.save(tbRp945);*/


    }

}
