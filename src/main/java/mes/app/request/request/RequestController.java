package mes.app.request.request;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mes.app.request.request.service.RequestService;
import mes.config.Settings;
import mes.domain.entity.User;
import mes.domain.entity.actasEntity.*;
import mes.domain.model.AjaxResult;
import mes.domain.repository.actasRepository.TB_DA006WRepository;
import mes.domain.repository.actasRepository.TB_DA007WRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import mes.app.UtilClass;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/request/request")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private TB_DA007WRepository tbda007WRepository;

    @Autowired
    private TB_DA006WRepository tbda006WRepository;

    @Autowired
    Settings settings;
    // 주문등록 세부사항 그리드 read
    @GetMapping("/read")
    public AjaxResult getList(@RequestParam Map<String, String> params
                            , Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = requestService.getUserInfo(username);
        TB_DA006W_PK tbDa006WPk = new TB_DA006W_PK();
        tbDa006WPk.setReqnum(params.get("reqnum"));
        tbDa006WPk.setSpjangcd("ZZ");
        tbDa006WPk.setCustcd((String) userInfo.get("custcd"));
        List<Map<String, Object>> items = this.requestService.getInspecList(tbDa006WPk);

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }
    // 주문의뢰현황 그리드 read
    @GetMapping("/order_read")
    public AjaxResult getOrderList(Authentication auth) {
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = requestService.getUserInfo(username);
        TB_DA006W_PK tbDa006WPk = new TB_DA006W_PK();
        tbDa006WPk.setSpjangcd("ZZ");
        tbDa006WPk.setCustcd((String) userInfo.get("custcd"));
        List<Map<String, Object>> items = this.requestService.getOrderList(tbDa006WPk);
//        Map<String, Object> headitem = this.requestService.getHeadList(tbDa006WPk);
//        items.add(headitem);
        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }
    //신규등록
    @Transactional
    @PostMapping("/save")
    public AjaxResult saveOrder(@RequestParam Map<String, String> params,
                                @RequestParam(value = "filelist", required = false) MultipartFile[] files,
                                @RequestPart(value = "deletedFiles", required = false) MultipartFile[] deletedFiles,
                                Authentication auth) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = (User) auth.getPrincipal();
        AjaxResult result = new AjaxResult();
        String username = user.getUsername();
        Map<String, Object> userInfo = requestService.getUserInfo(username);

        TB_DA006W_PK headpk = new TB_DA006W_PK();
        headpk.setCustcd((String) userInfo.get("custcd"));
        headpk.setSpjangcd("ZZ");
        headpk.setReqdate(params.get("reqdate").replaceAll("-",""));
        String reqnum = String.valueOf(tbda006WRepository.findMaxReqnum((String) userInfo.get("custcd"),"ZZ",params.get("reqdate")) + 1);
        headpk.setReqnum(reqnum);

        TB_DA006W tbDa006 = new TB_DA006W();

        if(files != null){
            for (MultipartFile multipartFile : files) {
                String path = settings.getProperty("file_upload_path") + "주문등록";
                MultipartFile file = multipartFile;
                float fileSize = (float) file.getSize();

                if(fileSize > 52428800){
                    result.message = "파일의 크기가 초과하였습니다.";
                    return result;
                }
                String fileName = file.getOriginalFilename();
                String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                String file_uuid_name = UUID.randomUUID().toString() + "." + ext;
                String saveFilePath = path;
                File saveDir = new File(saveFilePath);



                //디렉토리 없으면 생성
                if(!saveDir.isDirectory()){
                    saveDir.mkdirs();
                }
                File saveFile = new File(path + File.separator + file_uuid_name);
                file.transferTo(saveFile);
                TB_DA006WFile tbDa006WFile = new TB_DA006WFile();

                tbDa006WFile.setFilepath(saveFilePath);
                tbDa006WFile.setFilesvnm(file_uuid_name);
                tbDa006WFile.setFileornm(fileName);
                tbDa006WFile.setFilesize(fileSize);
                tbDa006WFile.setCustcd((String) userInfo.get("custcd"));
                tbDa006WFile.setSpjangcd("ZZ");
                tbDa006WFile.setReqdate(params.get("reqdate").replaceAll("-",""));
                tbDa006WFile.setReqnum(reqnum);
                tbDa006WFile.setIndatem(params.get("reqdate").replaceAll("-",""));
                tbDa006WFile.setInuserid(String.valueOf(user.getId()));
                tbDa006WFile.setInusernm(username);
                tbDa006WFile.setFileextns(ext);
                tbDa006WFile.setFileurl(saveFilePath);

                if (!requestService.saveFile(tbDa006WFile)) {
                    result.success = false;
                    result.message = "저장에 실패하였습니다.";
                    break;
                }
            }
        }
        // 삭제된 파일 처리
        if (deletedFiles != null && deletedFiles.length > 0) {
            List<TB_RP760> tbRp760List = new ArrayList<>();
            for (MultipartFile deletedFile : deletedFiles) {
                String content = new String(deletedFile.getBytes(), StandardCharsets.UTF_8);
                Map<String, String> deletedFileMap = new ObjectMapper().readValue(content, Map.class);

                //TB_RP760 tbRp760 =
//                if (tbRp760 != null) {
//                    // 파일 삭제
//                    String filePath = tbRp760.getFilepath();
//                    String fileName = tbRp760.getFilesvnm();
//                    File file = new File(filePath, fileName);
//                    if (file.exists()) {
//                        file.delete();
//                    }
//                    tbRp760List.add(tbRp760);
//                }
            }
            //TBRP760Repository.deleteAll(tbRp760List);
        }

        tbDa006.setPk(headpk);
        tbDa006.setCltcd((String) userInfo.get("cltcd"));
        tbDa006.setCltnm((String) userInfo.get("cltnm"));
        tbDa006.setSaupnum((String) userInfo.get("cltcd"));

        tbDa006.setCltzipcd(params.get("postno"));
        tbDa006.setCltaddr(params.get("address1"));
        tbDa006.setDeldate(params.get("deldate").replaceAll("-",""));
        tbDa006.setPerid(params.get("perid"));      // 담당자명
        tbDa006.setPanel_ht(params.get("panel_ht"));
        tbDa006.setPanel_hw(params.get("panel_hw"));
        tbDa006.setPanel_hl(params.get("panel_hl"));
        tbDa006.setPanel_hh(params.get("panel_hh"));
        tbDa006.setIndate(params.get("deldate").replaceAll("-",""));
        tbDa006.setInperid(String.valueOf(user.getId()));
        tbDa006.setTelno(params.get("telno"));
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
            if (bodyDataJson != null && !bodyDataJson.isEmpty()) {
                // JSON 배열을 List<Map<String, Object>>로 변환
                List<Map<String, Object>> jsonDataList = objectMapper.readValue(
                        bodyDataJson, new TypeReference<List<Map<String, Object>>>() {}
                );
                // Map을 통해 필드에 접근
                bodypk.setCustcd((String) userInfo.get("custcd"));
                bodypk.setSpjangcd("ZZ");
                bodypk.setReqdate(params.get("reqdate").replaceAll("-",""));
                bodypk.setReqnum(reqnum);
                List<String> reqseqList = tbda007WRepository.findReqseq(reqnum, (String) userInfo.get("custcd"),"ZZ",params.get("reqdate").replaceAll("-",""));
                for (Map<String, Object> jsonData : jsonDataList) {
                    if (!reqseqList.contains((String) jsonData.get("reqseq"))) {
                        String reqseq = String.valueOf(tbda007WRepository.findMaxReqseq(reqnum, (String) userInfo.get("custcd"), "ZZ", params.get("reqdate").replaceAll("-","")) + 1);
                        bodypk.setReqseq(reqseq);

                        tbDa007.setPk(bodypk);
                        tbDa007.setHgrb((String) jsonData.get("hgrb"));
                        tbDa007.setPanel_t((String) jsonData.get("panel_t"));
                        tbDa007.setPanel_w((String) jsonData.get("panel_w"));
                        tbDa007.setPanel_l((String) jsonData.get("panel_l"));
                        tbDa007.setQty((String) jsonData.get("qty"));
                        tbDa007.setExfmtypedv((String) jsonData.get("exfmtypedv"));
                        tbDa007.setInfmtypedv((String) jsonData.get("infmtypedv"));
                        tbDa007.setStframedv((String) jsonData.get("stframedv"));
                        tbDa007.setStexplydv((String) jsonData.get("stexplydv"));
                        tbDa007.setOrdtext((String) jsonData.get("ordtext"));
                        tbDa007.setIndate(params.get("deldate").replaceAll("-",""));
                        tbDa007.setInperid(String.valueOf(user.getId()));

                        boolean successcodebody = requestService.saveBody(tbDa007);
                        if (successcodebody) {
                            result.success = true;
                            result.message = "저장하였습니다.";
                        } else {
                            result.success = false;
                            result.message = "세부사항 저장에 실패하였습니다.";
                        }
                    } else {
                        boolean successcodebody = requestService.updateBody(tbDa007);
                        if (successcodebody) {
                            result.success = true;
                            result.message = "수정하였습니다.";
                        } else {
                            result.success = false;
                            result.message = "세부사항 수정에 실패하였습니다.";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.success = false;
            result.message = "JSON 파싱 오류";
            return result;
        }

        return result;
    }

    @PostMapping("/downloader")
    public ResponseEntity<?> downloadFile(@RequestBody List<Map<String, Object>> reqnums) throws IOException {

        // 파일 목록과 파일 이름을 담을 리스트 초기화
        List<File> filesToDownload = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();

        // ZIP 파일 이름을 설정할 변수 초기화
        String tketcrdtm = null;
        String tketnm = null;

        // 파일을 메모리에 쓰기
        for (Map<String, Object> reqnum : reqnums) {
            // 다운로드 위한 파일 정보 조회
            List<Map<String, Object>> fileList = requestService.download(reqnum);

            for (Map<String, Object> fileInfo : fileList) {
                String filePath = (String) fileInfo.get("filepath");    // 파일 경로
                String fileName = (String) fileInfo.get("filesvnm");    // 파일 이름(uuid)
                String originFileName = (String) fileInfo.get("fileornm");  //파일 원본이름(origin Name)

                if (tketcrdtm == null) {
                    tketcrdtm = (String) fileInfo.get("tketcrdtm");
                }
                if (tketnm == null) {
                    tketnm = (String) fileInfo.get("tketnm");
                }

                File file = new File(filePath + File.separator + fileName);

                // 파일이 실제로 존재하는지 확인
                if (file.exists()) {
                    filesToDownload.add(file);
                    fileNames.add(originFileName); // 다운로드 받을 파일 이름을 originFileName으로 설정
                }
            }
        }

        // 파일이 없는 경우
        if (filesToDownload.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 파일이 하나인 경우 그 파일을 바로 다운로드
        if (filesToDownload.size() == 1) {
            File file = filesToDownload.get(0);
            String originFileName = fileNames.get(0); // originFileName 가져오기

            HttpHeaders headers = new HttpHeaders();
            String encodedFileName = URLEncoder.encode(originFileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originFileName + "\"; filename*=UTF-8''" + encodedFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(file.length());

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(file.toPath()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        }

        String zipFileName = (tketcrdtm != null && tketnm != null) ? tketcrdtm + "_" + tketnm + ".zip" : "download.zip";

        // 파일이 두 개 이상인 경우 ZIP 파일로 묶어서 다운로드
        ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(zipBaos)) {

            for (int i = 0; i < filesToDownload.size(); i++) {
                File file = filesToDownload.get(i);
                String originFileName = fileNames.get(i); // originFileName 가져오기

                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(originFileName);
                    zipOut.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zipOut.write(buffer, 0, len);
                    }

                    zipOut.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            }

            zipOut.finish();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        ByteArrayResource zipResource = new ByteArrayResource(zipBaos.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        String encodedZipFileName = URLEncoder.encode(zipFileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName + "\"; filename*=UTF-8''" + encodedZipFileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(zipResource.contentLength());

        return ResponseEntity.ok()
                .headers(headers)
                .body(zipResource);
    }
    //제품구성 리스트 불러오는 function
    @GetMapping("/getListHgrb")
    public AjaxResult getListHgrb(){
        List<Map<String, Object>> items = this.requestService.getListHgrb();

        AjaxResult result = new AjaxResult();
        result.data = items;
        return result;
    }
    //보강재, 마감재 리스트 불러오는 function
    @GetMapping("/getListCgrb")
    public AjaxResult getListCgrb(){
        List<Map<String, Object>> items = this.requestService.getListCgrb();

        AjaxResult result = new AjaxResult();
        result.data = items;
        return result;
    }
    // 유저정보 불러와 input태그 value
    @GetMapping("/getUserInfo")
    public AjaxResult getUserInfo(Authentication auth){
        User user = (User) auth.getPrincipal();
        String username = user.getUsername();
        Map<String, Object> userInfo = requestService.getMyInfo(username);

        AjaxResult result = new AjaxResult();
        result.data = userInfo;
        return result;
    }
//    @PostMapping("/uploadEditor")
//    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file
//                                        , HttpServletRequest request
//                                        , HttpServletResponse response) {
//        String uploadDir = "c:\\temp\\editorFile\\";
//        // 디렉토리 확인 및 생성
//        File directory = new File(uploadDir);
//        if (!directory.exists()) {
//            directory.mkdirs(); // 디렉토리 생성
//        }
//        // 파일 저장
//        try {
//            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename(); // 파일 이름 중복 방지
//            File destinationFile = new File(uploadDir + fileName);
//            file.transferTo(destinationFile);
//
//            // 현재 요청의 프로토콜 및 호스트 주소 가져오기
//            String baseUrl = request.getScheme() + "://" + request.getServerName()
//                    + ":" + request.getServerPort();
//
//            // 웹 URL을 포함한 파일 경로 생성
//            String fileUrl = baseUrl + "/images/" + fileName;
//            File target = new File(uploadDir + fileName);
//            file.transferTo(target);
//            // 이미지 URL 반환
//            String imageUrl = uploadDir + fileName;
//            // JSON 응답 생성
//            JSONObject jsonResponse = new JSONObject();
//            jsonResponse.put("location", imageUrl);
//
//            // 응답에 JSON 전송
//            response.setContentType("application/json");
//            response.setCharacterEncoding("UTF-8");
//            response.getWriter().write(jsonResponse.toString());
//
//
//            return ResponseEntity.ok(Collections.singletonMap("location", fileUrl));
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Collections.singletonMap("error", "파일 업로드 실패: " + e.getMessage()));
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//    }
    @RequestMapping(value="/uploadImage")   //공지사항 이미지 등록
    public void UploadImage ( @RequestPart(value = "file",required = false) List<MultipartFile> file
            , Model model
            , HttpServletRequest request
            , HttpServletResponse response) throws IOException {
        String ls_fileName = "";
        String ls_errmsg = "";
        String imageUrl = "";

        String _uploadPath = Paths.get("C:", "temp", "editor_file").toString();
        /* uploadPath에 해당하는 디렉터리가 존재하지 않으면, 부모 디렉터리를 포함한 모든 디렉터리를 생성 */
        File dir = new File(_uploadPath);
        if (dir.exists() == false) {
            dir.mkdirs();
        }

        try {

            for(MultipartFile multipartFile : file){
                ls_fileName = multipartFile.getOriginalFilename();


                /* 파일이 비어있으면 비어있는 리스트 반환 */
                if (multipartFile.getSize() < 1) {
                    ls_errmsg = "success";
                    return ;
                }
                /* 파일 확장자 */
                final String extension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
                /* 서버에 저장할 파일명 (랜덤 문자열 + 확장자) */
                final String saveName = ls_fileName;

                /* 업로드 경로에 saveName과 동일한 이름을 가진 파일 생성 */
                File target = new File(_uploadPath, saveName);
                multipartFile.transferTo(target);
                _uploadPath = _uploadPath + "\\";
                // 이미지 URL 반환
                imageUrl = _uploadPath + saveName;
                // JSON 응답 생성
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("location", imageUrl);

                // 응답에 JSON 전송
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse.toString());
            }

        }catch (DataAccessException e){
            throw e;
        } catch (Exception  e){
                /*log.info("memberUpload Exception ================================================================");
                log.info(e.toString());
                ls_errmsg = "[" + ls_fileName + "] failed to save";
                throw new AttachFileException("[" + ls_fileName + "] failed to save");*/
            //utils.showMessageWithRedirect("시스템에 문제가 발생하였습니다", "/app05/App05list/", Method.GET, model);
        }
        return ;
    //        utils.showMessageWithRedirect("게시글 등록이 완료되었습니다", "/app05/App05list/", Method.GET, model);
    }
    // 삭제 메서드
    @DeleteMapping("/delete")
    public AjaxResult deleteElecSafe(@RequestBody List<TB_RP750_PK> pkList) {
        AjaxResult result = new AjaxResult();

        for (TB_RP750_PK pk : pkList) {

            if (pk.getCheckdt() != null) {
                pk.setCheckdt(pk.getCheckdt().replaceAll("-", ""));
            }

            //boolean success = requestService.delete(pk);

//            if (success) {
//                result.success = true;
//                result.message = "삭제하였습니다.";
//            } else {
//                result.success = false;
//                result.message = "삭제에 실패하였습니다.";
//            }
        }
        return result;
    }
}

