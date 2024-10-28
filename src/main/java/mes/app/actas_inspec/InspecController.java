package mes.app.actas_inspec;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
import mes.app.UtilClass;
import mes.app.actas_inspec.service.FileUploaderService;
import mes.app.actas_inspec.service.InspecService;
import mes.app.common.service.FileService;
import mes.config.Settings;
import mes.domain.DTO.Actas_Fileset;
import mes.domain.entity.AttachFile;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_INSPEC;
import mes.domain.entity.actasEntity.TB_RP710;
import mes.domain.entity.actasEntity.TB_RP715;
import mes.domain.model.AjaxResult;
import mes.domain.repository.AttachFileRepository;
import mes.domain.repository.actasRepository.TB_INSPECRepository;
import mes.domain.repository.actasRepository.TB_RP715Repository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xpath.objects.XObject;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/inspec_report")
public class InspecController {

    @Autowired
    FileService attachFileService;

    @Autowired
    AttachFileRepository attachFileRepository;

    @Autowired
    FileUploaderService FileService;

}
