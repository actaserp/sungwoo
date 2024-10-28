package mes.app.system;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.jbig2.util.log.Logger;
import org.apache.pdfbox.jbig2.util.log.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mes.app.system.service.SystemService;
import mes.domain.entity.User;
import mes.domain.model.AjaxResult;
import mes.domain.services.SqlRunner;

@RequestMapping("/api/system")
@RestController
public class SystemController {
	
	@Autowired
	SystemService systemService;
	
	@Autowired
	SqlRunner sqlRunner;

	private static final Logger logger = LoggerFactory.getLogger(SystemController.class);
	AjaxResult result = new AjaxResult();
	@SuppressWarnings("unchecked")
	@GetMapping("/menus")
	public AjaxResult menus() {	
		try {
			SecurityContext sc = SecurityContextHolder.getContext();
			Authentication auth = sc.getAuthentication();
			User user = (User) auth.getPrincipal();

			List<Map<String, Object>> items = this.systemService.getWebMenuList(user);

			Map<Integer, Object> nodeMap = new HashMap<Integer, Object>();
			List<Map<String, Object>> menuItems = new ArrayList<>();

			for (int idx = 0; idx < items.size(); idx++) {
				Map<String, Object> dicData = items.get(idx);
				Integer id = (Integer) dicData.get("id");
				String menu_code = (String) dicData.get("menu_code");
				Integer pid = (Integer) dicData.get("pid");
				String name = (String) dicData.get("name");
//				boolean isbookmark = (boolean) dicData.get("isbookmark"); 0값을 boolean으로 직접 캐스팅 함.
				boolean isbookmark = (dicData.get("isbookmark") != null) && ((int) dicData.get("isbookmark") != 0);

				//        	String css = (String)dicData.get("css");
				Integer frontfolder_id = (Integer) dicData.get("frontfolder_id");

				//String log = String.format("id:%s, menu_code:%s, pid:%s, name:%s",id,menu_code, pid, name);
				//System.out.println(log);
				if (id != null) {
					List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();

					Map<String, Object> folder = new HashMap<>();
					folder.put("folder_id", id);
					folder.put("folder_name", name);
					folder.put("frontfolder_id", frontfolder_id);
					//        		folder.put("objUrl", "");
					//        		folder.put("menuIconCls", css);
					folder.put("nodes", nodes);
					folder.put("ismanual", false);
					//        		folder.put("isbookmark", false);
					folder.put("menuDepth", 1);

					menuItems.add(folder);
					nodeMap.put(id, nodes);
				} else {
					String url = String.format("/gui/%s", menu_code);
					List<Map<String, Object>> nodes = (ArrayList<Map<String, Object>>) nodeMap.get(pid);

					Map<String, Object> menuItem = new HashMap<>();
					menuItem.put("objId", menu_code);
					menuItem.put("objNm", name);
					menuItem.put("objUrl", url);
					menuItem.put("pid", pid);
					//        		menuItem.put("menuIconCls", css);
					//        		menuItem.put("nodes", new ArrayList<Map<String, Object>>());
					menuItem.put("ismanual", false);
					menuItem.put("isbookmark", isbookmark);
					menuItem.put("menuDepth", 2);

					nodes.add(menuItem);
				}

			}

			result.success = true;
			result.data = menuItems;
		} catch (ArithmeticException e) {
			// 특정 예외 처리
			logger.error( "menus01==>" + e.getMessage());
		} catch (Exception e) {
			// 일반 예외 처리
			logger.error( "menus02==>" + e.getMessage());
		} finally {
			// 선택 사항: try-catch 블록 후에 반드시 실행할 코드
//			logger.info("Process completed.");
			return result;
		}
	}

	@GetMapping("/bookmark")
	public AjaxResult bookmark() {	
		
        SecurityContext sc = SecurityContextHolder.getContext();
        Authentication auth = sc.getAuthentication();         
        User user = (User)auth.getPrincipal();
       
        List<Map<String, Object>> items = this.systemService.getBookmarkList(user.getId());
        AjaxResult result = new AjaxResult();
        result.data = items;
        result.success = true;
		return result;
	}	

	@PostMapping("/bookmark/save")
	public AjaxResult bookmarkSave(
			@RequestParam(value="menucode") String menucode,
			@RequestParam(value="isbookmark", required = false) String isbookmark,
			Authentication auth
			) {	
		
		User user = (User)auth.getPrincipal();        
        AjaxResult result = new AjaxResult();
        result.data = this.systemService.saveBookmark(menucode, isbookmark, user);
        result.success = true;
		return result;
	}	
	
	@GetMapping("/storyboard")
	public AjaxResult storyBoard() {	

        List<Map<String, Object>> items = this.systemService.storyBoard();
        
        AjaxResult result = new AjaxResult();
        result.data = items;
        result.success = true;
		return result;
	}	

}
