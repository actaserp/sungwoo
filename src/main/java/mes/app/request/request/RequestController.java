package mes.app.request.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mes.app.request.request.service.RequestService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.*;
import mes.domain.model.AjaxResult;
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
        ObjectMapper objectMapper = new ObjectMapper();
        User user = (User) auth.getPrincipal();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        AjaxResult result = new AjaxResult();
        String newKey = "";
        String nspworkcd = params.get("spworkcd");
        String nspcompcd = params.get("spcompcd");

        TB_DA006W_PK headpk = new TB_DA006W_PK();
        headpk.setCustcd("ZZ");
        headpk.setSpjangcd("ZZ");
        headpk.setReqdate(params.get("reqdate"));
        headpk.setReqnum(params.get("reqnum"));

        TB_DA006W tbDa006 = new TB_DA006W();

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

//            tbDa006.setFilepath(saveFilePath);
//            tbDa006.setFilesvnm(file_uuid_name);
//            tbDa006.setFileornm(fileName);
//            tbDa006.setFilesize(fileSize);
//            tbRp920.setFilerem();
        }

        tbDa006.setPk(headpk);
        tbDa006.setCltcd(params.get("cltcd"));
        tbDa006.setCltnm(params.get("cltnm"));
        tbDa006.setSaupnum(params.get("saupnum"));
        tbDa006.setCltzipcd(params.get("postno"));
        tbDa006.setCltaddr(params.get("address1"));
        tbDa006.setCltaddr02(params.get("address2"));
        tbDa006.setDeladdr(params.get("deladdr"));
        tbDa006.setDeldate(params.get("deldate"));
        tbDa006.setPerid(params.get("perid"));
        tbDa006.setPanel_ht(params.get("panel_ht"));
        tbDa006.setPanel_hw(params.get("panel_hw"));
        tbDa006.setPanel_hl(params.get("panel_hl"));
        tbDa006.setIndate(String.valueOf(now));
        tbDa006.setInperid(String.valueOf(user.getId()));
        tbDa006.setTelno(params.get("mcltusrhp"));
        tbDa006.setRemark(params.get("remark"));
        //tbDa006.setOrdtext(now);

        boolean successcode = requestService.save(tbDa006);
        if (successcode) {
            result.success = true;
            result.message = "저장하였습니다.";
        } else {
            result.success = false;
            result.message = "저장에 실패하였습니다.";
        }
        TB_DA007W_PK bodypk = new TB_DA007W_PK();

        TB_DA007W tbDa007 = new TB_DA007W();

        // 'bodyData' 필드를 JSON 문자열로 받아오기
        try {
            String bodyDataJson = params.get("bodyData");

            if (bodyDataJson != null) {
                // JSON 문자열을 Map으로 변환
                Map<String, Object> jsonData = objectMapper.readValue(bodyDataJson, new TypeReference<Map<String, Object>>() {});


                // Map을 통해 필드에 접근
                bodypk.setCustcd("ZZ");
                bodypk.setSpjangcd("ZZ");
                bodypk.setReqdate(params.get("bodydata"));
                bodypk.setReqnum(params.get("reqnum"));
                bodypk.setReqseq(findMaxReqseq(params.get("reqnum")));

                tbDa007.setPk(bodypk);
                tbDa007.setHgrb((String) jsonData.get("hgrb"));
                tbDa007.setPanel_t((String) jsonData.get("panel_t"));
                tbDa007.setPanel_w((String)jsonData.get("panel_w"));
                tbDa007.setPanel_l((String)jsonData.get("panel_l"));
                tbDa007.setQty((String) jsonData.get("qty"));
                tbDa007.setExfmtypedv((String)jsonData.get("exfmtypedv"));
                tbDa007.setInfmtypedv((String)jsonData.get("infmtypedv"));
                tbDa007.setStframedv((String)jsonData.get("stframedv"));
                tbDa007.setStexplydv((String)jsonData.get("stexplydv"));
                tbDa007.setRemark((String)jsonData.get("remarkrequest"));
                tbDa007.setIndate(String.valueOf(now));
                tbDa007.setInperid(String.valueOf(user.getId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.success = false;
            result.message = "JSON 파싱 오류";
            return result;
        }
        boolean successcodebody = requestService.saveBody(tbDa007);
        if (successcodebody) {
            result.success = true;
            result.message = "세부사항 저장하였습니다.";
        } else {
            result.success = false;
            result.message = "세부사항 저장에 실패하였습니다.";
        }
        return result;
    }

    public String findMaxReqseq(String reqnum) {
        int reqseq = tb_da007WRepository.findMaxReqseq(reqnum);

        return String.valueOf(reqseq + 1);
    }
}
