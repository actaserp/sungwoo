package mes.app.request.request;

import mes.app.operate.service.PowerService;
import mes.app.request.request.service.RequestService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.TB_RP920;
import mes.domain.entity.actasEntity.TB_RP920_PK;
import mes.domain.model.AjaxResult;
import mes.domain.repository.TB_RP920Repository;
import mes.domain.repository.actasRepository.TB_DA007WRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/api/request/request")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private TB_DA007WRepository tb_da007WRepository;

    @Autowired
    Settings settings;

    @GetMapping("/read")
    public AjaxResult getList() {

        List<Map<String, Object>> items = this.requestService.getInspecList();

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    @PostMapping("/save")
    public AjaxResult savePower(@RequestParam Map<String, String> params,
                                @RequestParam(value = "filelist", required = false) MultipartFile files,
                                Authentication auth) throws IOException {

        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AjaxResult result = new AjaxResult();
        String newKey = "";
        String nspworkcd = params.get("spworkcd");
        String nspcompcd = params.get("spcompcd");

//        if (nspworkcd != null && nspcompcd != null) {
//
//            Optional<String> checknovalue = tb_da007WRepository.findMaxChecknoBySpplancd(nspworkcd, nspcompcd);
//            if (checknovalue.isPresent()) {
//                Integer checknointvalue = Integer.parseInt(checknovalue.get()) + 1;
//                newKey = String.format("%03d", checknointvalue);
//            } else {
//                newKey = "001";
//            }
//
//        }

        TB_RP920_PK pk = new TB_RP920_PK();
        pk.setSpworkcd(nspworkcd);
        pk.setSpcompcd(nspcompcd);
        pk.setSpplancd(newKey);

        TB_RP920 tbRp920 = new TB_RP920();

        if(files != null){

            String path = settings.getProperty("file_upload_path") + "발전소";

            float fileSize = (float) files.getSize();

            if(fileSize > 52428800){
                result.message = "파일의 크기가 초과하였습니다.";
                return result;
            }

            String fileName = files.getOriginalFilename();
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            String file_uuid_name = UUID.randomUUID().toString() + "." + ext;
            String saveFilePath = path;
            File saveDir = new File(saveFilePath);
            MultipartFile mFile = null;

            mFile = files;

            //디렉토리 없으면 생성
            if(!saveDir.isDirectory()){
                saveDir.mkdirs();
            }

            File saveFile = new File(path + File.separator + file_uuid_name);
            mFile.transferTo(saveFile);

            tbRp920.setFilepath(saveFilePath);
            tbRp920.setFilesvnm(file_uuid_name);
            tbRp920.setFileornm(fileName);
            tbRp920.setFilesize(fileSize);
//            tbRp920.setFilerem();
        }

        tbRp920.setPk(pk);
        tbRp920.setSpworknm(params.get("spworknm"));
        tbRp920.setSpcompnm(params.get("spcompnm"));
        tbRp920.setSpplannm(params.get("spplannm"));
        tbRp920.setSpwtycd(params.get("spwtycd"));
        tbRp920.setSpwtynm(params.get("spwtynm"));
        tbRp920.setMakercd(params.get("makercd"));
        tbRp920.setMakernm(params.get("makernm"));
        tbRp920.setSetupdt(params.get("setupdt"));
        tbRp920.setPwcapa(Double.parseDouble(params.get("pwcapa")));
        tbRp920.setPostno(params.get("postno"));
        tbRp920.setAddress1(params.get("address1"));
        tbRp920.setAddress2(params.get("address2"));
        tbRp920.setWorkyn(params.get("workyn"));
        tbRp920.setMcltcd(params.get("mcltcd"));
        tbRp920.setMcltnm(params.get("mcltnm"));
        tbRp920.setMcltusrnm(params.get("mcltusrnm"));
        tbRp920.setMcltusrhp(params.get("mcltusrhp"));
        tbRp920.setRemark(params.get("remark"));
        tbRp920.setIndatem(now);
        tbRp920.setInuserid(String.valueOf(user.getId()));
        tbRp920.setInusernm(user.getUsername());

        boolean successcode = requestService.save(tbRp920);
        if (successcode) {
            result.success = true;
            result.message = "저장하였습니다.";
        } else {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }

        return result;
    }
}
