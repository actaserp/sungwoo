package mes.app.holiday;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//공휴일 api 통신
@RestController
@RequestMapping("/api/holidays")
public class HolidayController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${Holidays.apiUrl}")
    private String apiUrl;

    @Value("${Holidays.serviceKey}")
    private String serviceKey;

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getHolidaysForMultipleYears() {
        List<Map<String, String>> holidays = new ArrayList<>();
        int currentYear = java.time.Year.now().getValue(); // 현재 연도 가져오기

        try {
            // 전년도, 현재 연도, 다음 연도 처리
            for (int year : new int[]{currentYear - 1, currentYear, currentYear + 1}) {
                holidays.addAll(fetchHolidaysForYear(year)); // 연도별 공휴일 추가
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(holidays);
    }

    private List<Map<String, String>> fetchHolidaysForYear(int year) {
        String url = apiUrl + "?serviceKey=" + serviceKey + "&solYear=" + year + "&numOfRows=30";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        List<Map<String, String>> holidays = new ArrayList<>();

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                // XML 파싱
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(response.getBody())));

                NodeList items = doc.getElementsByTagName("item");

                for (int i = 0; i < items.getLength(); i++) {
                    Element item = (Element) items.item(i);
                    String locdate = item.getElementsByTagName("locdate").item(0).getTextContent();
                    String dateName = item.getElementsByTagName("dateName").item(0).getTextContent();

                    Map<String, String> holiday = new HashMap<>();
                    holiday.put("date", locdate);
                    holiday.put("name", dateName);

                    holidays.add(holiday);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse holidays for year: " + year, e);
            }
        }
        return holidays;
    }
}