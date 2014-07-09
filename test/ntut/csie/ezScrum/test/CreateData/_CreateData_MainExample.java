package ntut.csie.ezScrum.test.CreateData;

import java.io.IOException;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;

public class _CreateData_MainExample {

	public static void main(String[] args) {
		Configuration configuration = new Configuration();	// 設定初始檔，讀取 config
		configuration.setTestMode(true);
		configuration.store();
		
		// ==================  CreatePrjoect Example  =====================
		CreateProject cp = new CreateProject(1);	// 建構給一個建立測試專案的個數
		cp.exeCreate();								// 呼叫執行的方法，執行建立測試專案
		// ==================  CreatePrjoect Example  =====================
		
		
		// ================== CreateRelease Example =======================
//		CreateRelease cr = new CreateRelease(2, cp);	// 建構給一Release個數與CreateProject物件
//		cr.exe();
		// ================== CreateRelease Example =======================
		
		
		
		// ================== CreateSprint Example ======================
//		CreateSprint cs = new CreateSprint(5, cp);		// 建構給一Sprint個數與CreateProject物件
//		cs.exe();
		// ================== CreateSprint Example ======================
		
		
		
		// ================== CreateProductBacklog Example =================
//		CreateProductBacklog cpb = new CreateProductBacklog(10, cp);
//		cpb.exe();		
		// ================== CreateProductBacklog Example =================

		
		
		// ==================  CopyProject Example  =====================
		CopyProject CP = new CopyProject(cp);		// 將建立的測試專案作處理
		try {
			CP.exeCopy_Delete_Project();			// 刪除並且複製測試專案於桌面上，複製與還原 RoleBase.xml
//			CP.exeCopy_Project();					// 複製測試專案於桌面上，複製與還原 RoleBase.xml
//			CP.exeDelete_Project();					// 刪除測試專案於 TestWorkspace，複製與還原 RoleBase.xml
			configuration.setTestMode(false);
			configuration.store();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ==================  CopyProject Example  =====================
	}
}