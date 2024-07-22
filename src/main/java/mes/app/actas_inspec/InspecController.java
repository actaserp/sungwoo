package mes.app.actas_inspec;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringUtils;
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
import mes.domain.repository.actasRepository.TB_RP710Repository;
import mes.domain.repository.actasRepository.TB_RP715Repository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/inspec_report")
public class InspecController {

    @Autowired
    TB_RP715Repository tb_rp715Repository;

    @Autowired
    FileService attachFileService;

    @Autowired
    AttachFileRepository attachFileRepository;

    private final InspecService inspecService;
    private final Settings settings;
    private final TB_RP710Repository tb_rp710Repository;
    private final TB_INSPECRepository tB_INSPECRepository;


    public InspecController(InspecService inspecService, TB_RP710Repository tb_rp710Repository, Settings settings,
                            TB_INSPECRepository tB_INSPECRepository){
        this.inspecService = inspecService;
        this.tb_rp710Repository = tb_rp710Repository;
        this.settings = settings;
        this.tB_INSPECRepository = tB_INSPECRepository;
    }


    @GetMapping("/read")
    public AjaxResult getList(@RequestParam(value = "searchusr", required = false) String searchusr,
                              @RequestParam(value = "searchfrdate", required = false) String searchfrdate,
                              @RequestParam(value = "searchtodate", required = false) String searchtodate

                              ){
        List<Map<String, Object>> items = new ArrayList<>();

        searchusr = Optional.ofNullable(searchusr).orElse("");
        searchfrdate = Optional.ofNullable(searchfrdate).orElse("20000101");
        searchtodate = Optional.ofNullable(searchtodate).orElse("29991231");

        if(searchfrdate.isEmpty()){
            searchfrdate = "20000101";
        }
        if(searchtodate.isEmpty()){
            searchtodate = "29991231";
        }


        items = this.inspecService.getInspecList(searchusr, searchfrdate, searchtodate, "");

        AjaxResult result = new AjaxResult();
        result.data = items;

        return result;
    }

    //순회점검 일지 저장
    @PostMapping("/save")
    @Transactional
    public AjaxResult saveFilter(
            //@ModelAttribute TB_RP710 tbRp710
            @RequestParam(value = "supplier", required = false) String supplier,
            @RequestParam(value = "checkstdt", required = false) String checkstdt,
            @RequestParam(value = "checkdt", required = false) String checkdt,
            @RequestParam(value = "checkendt", required = false) String checkendt,
            @RequestParam(value = "checkusr", required = false) String checkusr,
            @RequestParam(value = "checkarea", required = false) String checkarea,
            @RequestParam(value = "randomuuid", required = false) String randomuuid,
            @RequestParam(value = "doc-list", required = false) List<String> doc_list,
            @RequestParam(value = "filelist", required = false) MultipartFile[] files
            //@RequestParam Map<String, String> params
            ){

        AjaxResult result = new AjaxResult();



        if(files != null){
            for(MultipartFile filelist : files){
                if(filelist.getSize() > 52428800){
                    result.success = false;
                    result.message = "파일사이즈가 초과하였습니다.";
                    return result;
                }
            }
        }

        TB_RP710 tbRp710dto = new TB_RP710();

        String checkdtconvertvalue = checkdt.replaceAll("-","");

        String formattedValue;
        Optional<String> checknovalue = tb_rp710Repository.findMaxChecknoByCheckdt(checkdtconvertvalue);
        if(checknovalue.isPresent()){

            Integer checknointvalue = Integer.parseInt(checknovalue.get()) + 1;

            formattedValue = String.format("%02d", checknointvalue);

        }else{
            formattedValue = "01";
        }

        tbRp710dto.setSpworkcd("001");
        tbRp710dto.setSpworknm("대구");
        tbRp710dto.setSpcompcd("001");
        tbRp710dto.setSpcompnm("대구성서공단");
        tbRp710dto.setSpplancd("001");
        tbRp710dto.setSpplannm("KT대구물류센터 연료전지발전소");
        tbRp710dto.setCheckdt(checkdtconvertvalue);
        tbRp710dto.setCheckno(formattedValue);
        tbRp710dto.setCheckstdt(checkstdt);
        tbRp710dto.setCheckendt(checkendt);
        tbRp710dto.setCheckusr(checkusr);
        tbRp710dto.setCheckarea(checkarea);

        tbRp710dto.setSupplier(supplier);
        tbRp710dto.setSpuncode(randomuuid);

            String path = settings.getProperty("file_upload_path") + "순회점검일지첨부파일";

            List<TB_RP715> fileEntities = new ArrayList<>();


        boolean successcode = inspecService.save(tbRp710dto, files, doc_list);
            if (successcode) {
                result.success = true;
                result.message = "저장하였습니다.";
            } else {
                result.success = false;
                result.message = "저장에 실패하였습니다.";
            }


        return result;
    }


    @PostMapping("/filesave")
    public AjaxResult fileupload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("spuncode") String spuncode){
        AjaxResult result = new AjaxResult();



        result.success = true;


        return result;
    }


    @PostMapping("/delete")
    @Transactional
    public AjaxResult delete(
            @RequestParam(value = "spuncode") String spuncode
    ){

        AjaxResult result = new AjaxResult();

        ObjectMapper mapper = new ObjectMapper();

        String cleanJson = spuncode.replaceAll("[\\[\\]\"]", "");
        String[] tokens = cleanJson.split(",");

        List<String> paramList = List.of(tokens);

        for(String param : paramList){
            System.out.println(param);
            //TODO: 이거 자식테이블먼저 삭제해야한다.
            tb_rp715Repository.deleteBySpuncodeId(param);
            tB_INSPECRepository.deleteBySpuncodeId(param);
            tb_rp710Repository.deleteBySpuncode(param);
        }


        result.success = true;
        result.message = "성공";
        return result;
    }

    @PostMapping("/download-docs")
    public void downloadDocs(HttpServletResponse response, @RequestBody List<String> selectedList) throws IOException {
        String path = settings.getProperty("file_upload_path") + "순회점검일지양식.docx";

        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(zipOutputStream);

        for (int i = 0; i < selectedList.size(); i++) {
            FileInputStream fis = new FileInputStream(path);
            XWPFDocument document = new XWPFDocument(fis);
            List<XWPFTable> tables = document.getTables();

            List<Map<String, Object>> rp710items = this.inspecService.getInspecList("", "", "", selectedList.get(i));
            List<Map<String, Object>> items = this.inspecService.getInspecDocList(selectedList.get(i));
            List<Map<String, Object>> FileItems = this.inspecService.getFileList(selectedList.get(i));

            if (tables.size() > 0) {
                for (int j = 1; j < tables.get(1).getRows().size(); j++) {
                    XWPFTableRow row = tables.get(1).getRow(j);
                    XWPFTableCell cell = row.getCell(1);
                    switch (j) {
                        case 1:
                            clearAndSetCellText(cell, rp710items.get(0).get("supplier").toString());
                            break;
                        case 2:
                            clearAndSetCellText(cell, rp710items.get(0).get("checkdt").toString());
                            break;
                        case 3:
                            clearAndSetCellText(cell, rp710items.get(0).get("checkusr").toString());
                            break;
                        case 4:
                            clearAndSetCellText(cell, rp710items.get(0).get("checkarea").toString());
                            break;
                        default:
                            clearAndSetCellText(cell, "");
                            break;
                    }
                } //2번째 테이블 작성완료 (1번째테이블은 결재테이블이라서 작성할 필요없음)


                //3번째 테이블 작성
                int itemValueIndex = 0;
                for(int k = 2; k < tables.get(2).getRows().size(); k++){
                        if(itemValueIndex >= items.size()) break;
                        XWPFTableRow row = tables.get(2).getRow(k);
                        XWPFTableCell cell = row.getCell(1);
                        System.out.println(k + "," + 1);
                        System.out.println(itemValueIndex);
                    Object insepcContValue = items.get(itemValueIndex).get("inspeccont");
                        String inspecContText = (insepcContValue != null) ? insepcContValue.toString() : "";

                        clearCellText(cell);
                        clearAndSetCellText(cell, inspecContText);

                        String cellText = cell.getText();
                        System.out.println("cellText :: " + cellText);
                        itemValueIndex++;
                }

                //개선사항 작성
                int itemValueIndex2 = 0;
                for(int k = 2; k < tables.get(2).getRows().size(); k++){
                    if(itemValueIndex2 >= items.size()) break;
                    XWPFTableRow row = tables.get(2).getRow(k);
                    XWPFTableCell cell = row.getCell(4);
                    System.out.println(k + "," + 2);
                    System.out.println(itemValueIndex2);
                    Object insepcContValue = items.get(itemValueIndex2).get("inspecreform");
                    String inspecContText = (insepcContValue != null) ? insepcContValue.toString() : "";

                    clearCellText(cell);
                    clearAndSetCellText(cell, inspecContText);

                    String cellText = cell.getText();
                    System.out.println("cellText :: " + cellText);
                    itemValueIndex2++;
                }

                //점검결과 작성 (O)
                int itemValueIndex3 = 0;
                for(int k = 2; k < tables.get(2).getRows().size(); k++){
                    if(itemValueIndex3 >= items.size()) break;
                    XWPFTableRow row = tables.get(2).getRow(k);
                    XWPFTableCell cell = row.getCell(2);
                    System.out.println(k + "," + 2);
                    System.out.println(itemValueIndex3);
                    Object insepcContValue = items.get(itemValueIndex3).get("inspecresult");
                    String inspecContText = (insepcContValue != null) ? insepcContValue.toString() : "";
                    String InsPecResultText = "";
                    if(inspecContText != null){
                        switch (inspecContText){
                            case "O": InsPecResultText = "O";
                            break;
                            case "X": InsPecResultText = "";
                        }
                    }

                    clearCellText(cell);
                    clearAndSetCellTextOX(cell, InsPecResultText);

                    String cellText = cell.getText();
                    System.out.println("cellText :: " + cellText);
                    itemValueIndex3++;
                }

                //점검결과 작성 (X)
                int itemValueIndex4 = 0;
                for(int k = 2; k < tables.get(2).getRows().size(); k++){
                    if(itemValueIndex4 >= items.size()) break;
                    XWPFTableRow row = tables.get(2).getRow(k);
                    XWPFTableCell cell = row.getCell(3);
                    System.out.println(k + "," + 3);
                    System.out.println(itemValueIndex4);
                    Object insepcContValue = items.get(itemValueIndex4).get("inspecresult");
                    String inspecContText = (insepcContValue != null) ? insepcContValue.toString() : "";
                    String InsPecResultText = "";
                    if(inspecContText != null){
                        switch (inspecContText){
                            case "O": InsPecResultText = "";
                                break;
                            case "X": InsPecResultText = "X";
                        }
                    }

                    clearCellText(cell);
                    clearAndSetCellTextOX(cell, InsPecResultText);

                    String cellText = cell.getText();
                    System.out.println("cellText :: " + cellText);
                    itemValueIndex4++;
                }




            }

            if(!FileItems.isEmpty()){
            for(int j=0; j < 2; j++){
                int index = 0;
                if(j==1) index=3;
                if(j+1 > FileItems.size()) break;
                String imagePath = settings.getProperty("file_upload_path") + "순회점검일지첨부파일/" + FileItems.get(j).get("filesvnm");


                XWPFTableRow firstRow = tables.get(3).getRow(index);
                XWPFTableCell firstCell = firstRow.getCell(0);

                XWPFTableRow SecondRow = tables.get(3).getRow(index+2);
                XWPFTableCell SecondCell = SecondRow.getCell(1);

                clearAndSetCellText(SecondCell, rp710items.get(0).get("checkarea").toString());


                clearCellText(firstCell);

                try(FileInputStream is = new FileInputStream(imagePath)){
                    XWPFParagraph paragraph = firstCell.addParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.addPicture(is, Document.PICTURE_TYPE_PNG, imagePath, Units.toEMU(500), Units.toEMU(300)); // 이미지 크기 설정 (100x100 EMU)
                } catch (InvalidFormatException e) {
                    throw new RuntimeException(e);
                }


            }




            }

            ByteArrayOutputStream documentOutputStream = new ByteArrayOutputStream();
            document.write(documentOutputStream);
            document.close();
            fis.close();

            byte[] documentBytes = documentOutputStream.toByteArray();
            String documentName = "modified_document_" + (i + 1) + ".docx";

            zos.putNextEntry(new ZipEntry(documentName));
            zos.write(documentBytes);
            zos.closeEntry();
        }

        zos.close();

        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=modified_documents.zip");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            zipOutputStream.writeTo(outputStream);
        }
    }


    //셀 지우고 줄바꿈
    private void clearAndSetCellText(XWPFTableCell cell, String text) {
        // 셀의 모든 문단을 제거합니다.
        int numParagraphs = cell.getParagraphs().size();
        for (int i = 0; i < numParagraphs; i++) {
            cell.removeParagraph(0);
        }

        // 텍스트를 '-' 기호로 나눕니다.
        String[] lines = text.split(" - ");

        // 각 부분을 새로운 문단으로 추가합니다.
        for (int i = 0; i < lines.length; i++) {
            XWPFParagraph paragraph = cell.addParagraph();
            XWPFRun run = paragraph.createRun();
            // 첫 줄은 그대로, 이후 줄은 '-' 기호를 앞에 붙입니다.
            if (i == 0) {
                run.setText(lines[i]);
            } else {
                run.setText("- " + lines[i]);
            }
        }
    }

    // 닥스에 OX적는게 있어서 분기별로 처리하려 생성하고 가운데 정렬
    private void clearAndSetCellTextOX(XWPFTableCell cell, String text) {
        // 셀의 모든 문단을 제거합니다.
        int numParagraphs = cell.getParagraphs().size();
        for (int i = 0; i < numParagraphs; i++) {
            cell.removeParagraph(0);
        }

        // 텍스트를 '-' 기호로 나눕니다.
        String[] lines = text.split(" - ");

        // 각 부분을 새로운 문단으로 추가합니다.
        for (int i = 0; i < lines.length; i++) {
            XWPFParagraph paragraph = cell.addParagraph();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            // 첫 줄은 그대로, 이후 줄은 '-' 기호를 앞에 붙입니다.
            if (i == 0) {
                run.setText(lines[i]);
            } else {
                run.setText("- " + lines[i]);
            }
        }
    }

    //셀에 있는 글자를 지움
    public void clearCellText(XWPFTableCell cell) {
        // 셀의 모든 문단을 제거합니다.
        int numParagraphs = cell.getParagraphs().size();
        for (int i = 0; i < numParagraphs; i++) {
            cell.removeParagraph(0);
        }
    }








}
