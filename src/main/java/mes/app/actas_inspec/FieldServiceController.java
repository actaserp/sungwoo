package mes.app.actas_inspec;


import com.fasterxml.jackson.databind.ObjectMapper;
import mes.app.UtilClass;
import mes.app.actas_inspec.service.FieldService;
import mes.app.actas_inspec.service.FileUploaderService;
import mes.domain.DTO.TB_RP810Dto;
import mes.domain.DTO.UserDto;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP815;
import mes.domain.model.AjaxResult;
import mes.domain.repository.UserRepository;
import mes.domain.repository.actasRepository.TB_RP810Repository;
import mes.domain.repository.actasRepository.TB_RP815Repository;
import org.apache.poi.ss.formula.functions.Offset;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/fieldService")
public class FieldServiceController {

    private final FieldService fieldService;
    private final UserRepository userRepository;
    private final TB_RP815Repository tbRp815Repository;
    private final TB_RP810Repository tbRp810Repository;


    public FieldServiceController(FieldService fieldService, UserRepository userRepository, TB_RP815Repository tbRp815Repository,
                                  TB_RP810Repository tbRp810Repository) {
        this.fieldService = fieldService;
        this.userRepository = userRepository;
        this.tbRp815Repository = tbRp815Repository;
        this.tbRp810Repository = tbRp810Repository;

    }


    @GetMapping("/fsresponList")
    public AjaxResult fsResponseList(){

        AjaxResult result = new AjaxResult();

        List<Map<String, Object>> item =  fieldService.getPRresponseList();

        result.data = item;
        result.success = true;
        return result;
    }

    @PostMapping("/save")
    public AjaxResult saveFilter(
            @RequestPart(value = "jsonData") TB_RP810Dto dto,
            @RequestParam(value = "fsresponnm") Integer fsresponnm,
            @RequestParam(value = "flag") String flag,
            @RequestPart(value = "filelist", required = false) MultipartFile[] files,
            @RequestPart(value = "deletedFiles", required = false) MultipartFile[] deletedFiles
    )throws Exception{
        AjaxResult result = new AjaxResult();

        String userid = new UtilClass().getUserId();
        String username = new UtilClass().getUsername();


        Optional<User> user = userRepository.findById(fsresponnm);
        dto.setInuserid(userid);
        dto.setInusernm(username);
        dto.setFsresponid(user.isPresent() ? user.get().getUsername() : "");
        dto.setFsresponnm(user.isPresent() ? user.get().getFirst_name() : "");

        if (deletedFiles != null && deletedFiles.length > 0){

            for(MultipartFile deletedFile : deletedFiles){
                String content = new String(deletedFile.getBytes(), StandardCharsets.UTF_8);
                Map<String, String> deleteFileMap = new ObjectMapper().readValue(content, Map.class);

                String checkseq = deleteFileMap.get("checkseq");
                String spuncode_id = deleteFileMap.get("spuncode_id");
                String filepath = deleteFileMap.get("filepath");
                String filesvnm = deleteFileMap.get("filesvnm");

                new FileUploaderService().deleteFileFromDisk(filepath, filesvnm);

                tbRp815Repository.deleteBySpuncodeIdAAndCheckseq(spuncode_id, checkseq);
            }
        }
        Boolean flag2 = false;

        if(flag.equals("S")){
            flag2 = fieldService.save(dto, files);
        }else{
            flag2 = fieldService.update(dto, files);
        }

        if(flag2){
            result.success = true;
            result.message = "저장하였습니다.";
        }else{
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }

        return result;
    }

    @GetMapping("/read")
    public AjaxResult GetList(@RequestParam("searchfrdate") String searchfrdate,
                              @RequestParam("searchtodate") String searchtodate,
                              @RequestParam(value = "searchsitename", required = false) String searchsitename,
                              @RequestParam(value = "searchesname", required = false) String searchesname) throws ParseException {
        AjaxResult result = new AjaxResult();

        searchsitename = Optional.ofNullable(searchsitename).orElse("");
        searchesname = Optional.ofNullable(searchesname).orElse("");

        if(searchfrdate.isEmpty()){
            searchfrdate = "20000101";
        }
        if(searchtodate.isEmpty()){
            searchtodate = "29991231";
        }

        // 리스트 가져오기
        List<Map<String, Object>> items = fieldService.getList(searchfrdate, searchtodate, searchsitename, searchesname);

        result.data = items;
        result.success = true;

        return result;
    }

    @GetMapping("/fileList")
    public AjaxResult FileList(@RequestParam("spuncode") String spuncode){

        AjaxResult result = new AjaxResult();

        List<Map<String, Object>> items = fieldService.getFileList2(spuncode);



        result.success = true;
        result.data = items;
        return result;
    }

    @PostMapping("/delete")
    public AjaxResult delete(@RequestParam(value = "spuncode") String spuncode){

        AjaxResult result = new AjaxResult();

        List<String> paramList = new UtilClass().parseUserIds(spuncode);

        List<String> EntityList = new ArrayList<>();
        FileUploaderService fileservice = new FileUploaderService();


        for(String param : paramList){

            List<Map<String, Object>> FileItems = fieldService.getFileList(param);

            if(!FileItems.isEmpty()){
                for (Map<String, Object> fileItem : FileItems) {
                    fileservice.deleteFileFromDisk(fileItem.get("filepath").toString(), fileItem.get("filesvnm").toString());
                }
            }

            EntityList.add(param);

            //tbRp815Repository.deleteBySpuncodeId(param);
            //tb_rp710Repository.deleteBySpuncode(param);
        }
        tbRp815Repository.deleteBySpuncodeId(EntityList);
        tbRp810Repository.deleteBySpuncode(EntityList);

        result.success = true;
        result.message = "성공";
        return result;

    }

}
