package mes.app.actas_inspec.service;


import mes.app.actas_inspec.FileController;
import mes.config.Settings;
import mes.domain.entity.actasEntity.TB_INSPEC;
import mes.domain.entity.actasEntity.TB_RP710;
import mes.domain.entity.actasEntity.TB_RP715;
import mes.domain.repository.actasRepository.TB_INSPECRepository;
import mes.domain.repository.actasRepository.TB_RP715Repository;
import mes.domain.services.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@Service
public class InspecService {
    @Autowired
    SqlRunner sqlRunner;

    @Autowired
    Settings settings;

    @Autowired
    TB_INSPECRepository tb_inspecRepository;

    @Autowired
    FileUploaderService fileService;

    @Autowired
    FileController fileController;


}
