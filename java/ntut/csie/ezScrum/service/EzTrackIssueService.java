package ntut.csie.ezScrum.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.internal.Issue;
import ntut.csie.ezScrum.issue.internal.IssueTypeField;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.internal.AbstractMantisService;
import ntut.csie.ezScrum.issue.sql.service.internal.MySQLQuerySet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;

public class EzTrackIssueService extends AbstractMantisService {
	//private IIssue[] m_issues;
	//private String m_currentProjectName = "";
	
	public EzTrackIssueService(ISQLControl control, Configuration config) {
		setControl(control);
		setConfig(config);
	}
	
	
	/************************************************************
	 * 依照 Issue ID 取出 Issue
	 *************************************************************/
	public IIssue getIssue(long issueID) {
		IIssue issue = null;
		String category ="";
		/* 取出 Issue 預設欄位值 */
		IQueryValueSet valueSet = new MySQLQuerySet();
		// 訂定查詢table名稱
		valueSet.addTableName("mantis_bug_table");
		valueSet.addTableName("mantis_bug_text_table");
		// 訂定查詢的條件
		valueSet.addFieldEqualCondition("mantis_bug_table.bug_text_id",
				"mantis_bug_text_table.id");
		valueSet.addFieldEqualCondition("mantis_bug_table.id", 
				Long.toString(issueID));
		// 產生查詢的query
		String query = valueSet.getSelectQuery();
		//System.out.println("my1=" + query);
		try {
			ResultSet result = getControl().executeQuery(query);
			// 取得 Issue ID + ProjectID + 類型 + 名稱 + 描述
			if (result.next()) {
				issue = new Issue();
				issue.setIssueID(result.getLong("id"));
				//塞入project的名稱
				String projectName = getProjectName(result.getInt("project_id"));
				issue.setProjectID(result.getString("project_id"));
				issue.setProjectName(projectName);
				//塞入handler的名稱
				int handlerID = result.getInt("handler_id");
				if (handlerID > 0)
					issue.setAssignto(getUserName(handlerID));
				issue.setSummary(result.getString("summary"));
				//reporter名稱，若為""則判定為guest
				String userName = getUserName(result.getInt("reporter_id"));
				if(userName.equals(""))
					issue.setReporter("guest");
				else
					issue.setReporter(userName);
				issue.setDescription(result.getString("description"));
				issue.setCategory(result.getString("category"));
				//用project 名稱用以得到專案的客製化issue type
				List<CustomIssueType> issueTypes = getCustomIssueType(projectName);
				category = result.getString("category");
				try{
					long categoryID =  Long.parseLong(category);
					for(CustomIssueType issueType : issueTypes){
						if(issueType.getTypeId()==categoryID)
							issue.setCategory(issueType.getTypeName());
					}					
				} catch (NumberFormatException e) {
					//category不為數字, 故為scrum issue
					issue.setCategory(result.getString("category"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//設定客製化的內容
		getIssueTypeFields(issue, category);
		
		return issue;
	}
	
	
	
	
	/************************************************************
	 * 依照 project 名稱取得CustomIssueType的種類
	 *************************************************************/
	public List<CustomIssueType> getCustomIssueType(String projectName) {
		List<CustomIssueType> typeList = new ArrayList<CustomIssueType>();
		int projectID = getProjectID(projectName);
		
		IQueryValueSet valueSet = new MySQLQuerySet();
		// 訂定查詢table名稱
		valueSet.addTableName("eztrack_issuetype");
		// 訂定查詢的條件
		valueSet.addFieldEqualCondition("eztrack_issuetype.ProjectID",
				Integer.toString(projectID));
		// 產生查詢的query
		String query = valueSet.getSelectQuery();
		try {
			ResultSet result = getControl().executeQuery(query);
			// 取得 type ID + type名稱
			
			if (result == null ) {
				return null;
			}
			
			while (result.next()) {
				CustomIssueType type = new CustomIssueType();
				String typeId = result.getString("IssueTypeID");
				String typeName = result.getString("IssueTypeName");
				boolean isPublic = result.getBoolean("IsPublic");
				boolean isKanban = result.getBoolean("IsKanban");
				type.setTypeId(Long.parseLong(typeId));
				type.setTypeName(typeName);
				type.setPublic(isPublic);
				type.setKanban(isKanban);
				typeList.add(type);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return typeList;
	}
	
	
	/* 建立一個新的Issue type*/
	public CustomIssueType addIssueType(String projectName, String typeName, boolean isPublic) {
		CustomIssueType type = null;
		
		int projectID = getProjectID(projectName);
		//設定SQL
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("eztrack_issuetype");
		valueSet.addInsertValue("ProjectID", Integer.toString(projectID));
		valueSet.addInsertValue("IssueTypeName", typeName);
		if(isPublic)
			valueSet.addInsertValue("IsPublic", "1");
		else
			valueSet.addInsertValue("IsPublic", "0");
		//設定不為Kanban
		valueSet.addInsertValue("IsKanban", "0");
		String query = valueSet.getInsertQuery();
		getControl().execute(query, true);
		
		//清空
		valueSet.clear();
		/* 抓取剛才新增的資料 */
		valueSet.addTableName("eztrack_issuetype");
		// 訂定查詢的條件
		valueSet.addFieldEqualCondition("ProjectID",
				Integer.toString(projectID));
		valueSet.addFieldEqualCondition("IssueTypeName", "'"+typeName+"'");
		// 產生查詢的query
		query = valueSet.getSelectQuery();
		try {
			ResultSet result = getControl().executeQuery(query);
			type = new CustomIssueType();
			// 取得 type ID + type名稱
			while (result.next()) {
				String m_typeId = result.getString("IssueTypeID");
				String m_typeName = result.getString("IssueTypeName");
				boolean m_isPublic = result.getBoolean("IsPublic");
				boolean m_isKanban = result.getBoolean("IsKanban");
				type.setTypeId(Long.parseLong(m_typeId));
				type.setTypeName(m_typeName);
				type.setPublic(m_isPublic);
				type.setKanban(m_isKanban);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		//新增status的欄位
		IssueTypeField statusField = new IssueTypeField();
		statusField.setFieldName("Status");
		statusField.setFieldCategory("Combo");
		
		int typeFieldID = addTypefield(type, statusField);
		/* 預設status的combo資訊*/
		createDefaultComboFieldData(typeFieldID, "New");
//		createDefaultComboFieldData(typeFieldID, "Assigned");
		createDefaultComboFieldData(typeFieldID, "Closed");
		
		//新增priority的欄位
		IssueTypeField priorityField = new IssueTypeField();
		priorityField.setFieldName("Priority");
		priorityField.setFieldCategory("Combo");
		typeFieldID = addTypefield(type, priorityField);
		createDefaultComboFieldData(typeFieldID, "Low");
		createDefaultComboFieldData(typeFieldID, "Medium");
		createDefaultComboFieldData(typeFieldID, "High");
		
		//新增是否已經處理的欄位
		IssueTypeField handledField = new IssueTypeField();
		handledField.setFieldName("Handled");
		handledField.setFieldCategory("Textbox");
		addTypefield(type, handledField);
		
		//新增註解的欄位
		IssueTypeField commentField = new IssueTypeField();
		commentField.setFieldName("Comment");
		commentField.setFieldCategory("Textbox");
		addTypefield(type, commentField);
		
		//如果issue type設定為public時，則新增了reporter及聯絡方式這兩個客製化欄位
		if(isPublic){
			IssueTypeField field = new IssueTypeField();
			//新增reporter的欄位
			field.setFieldName("ReportUserName");
			field.setFieldCategory("Textbox");
			addTypefield(type, field);
			//新增聯絡方式
			field.setFieldName("Email");
			field.setFieldCategory("Textbox");
			addTypefield(type, field);
		}
		return type;
	}
	
	
	
	/************************************************************
	 * 依照 Issue ID 取出 TypeFields
	 *************************************************************/
	private void getIssueTypeFields(IIssue issue, String category) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		/* 取出 Issue 客製化欄位值 */
		valueSet.clear();
		// 訂定查詢table名稱
		valueSet.addTableName("eztrack_issuetype");
		valueSet.addTableName("eztrack_typefield");
		valueSet.addTableName("eztrack_typefieldvalue");
		// 訂定查詢的條件
		valueSet.addFieldEqualCondition("eztrack_issuetype.ProjectID",
				issue.getProjectID());
		valueSet.addFieldEqualCondition("eztrack_issuetype.IssueTypeID",
				category);
		valueSet.addFieldEqualCondition("eztrack_issuetype.IssueTypeID", 
				"eztrack_typefield.IssueTypeID");
		valueSet.addFieldEqualCondition("eztrack_typefieldvalue.IssueID", 
				String.valueOf(issue.getIssueID()));
		valueSet.addFieldEqualCondition("eztrack_typefield.TypeFieldID", 
				"eztrack_typefieldvalue.TypeFieldID");
		// 產生查詢的query
		String query = valueSet.getSelectQuery();
		//System.out.println("my2=" + query);
		try {
			ResultSet result = getControl().executeQuery(query);
			// 依據 Issue 類型取得該類型自訂的欄位名稱 + 此 Issue 的數值
			while (result!=null && result.next() && !issue.equals(null)) {
				IssueTypeField field = new IssueTypeField(result.getInt("TypeFieldID"),
						result.getString("TypeFieldName"),result.getString("TypeFieldCategory"),
						result.getString("FieldValue"));
				issue.addField(field);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 新增 Issue Type 的欄位
	 * @param type
	 * @param field
	 * @return
	 */
	private int addTypefield(CustomIssueType type, IssueTypeField field){
		//設定SQL
		IQueryValueSet valueSet = new MySQLQuerySet();

		valueSet.addTableName("eztrack_typefield");
		valueSet.addInsertValue("IssueTypeID", String.valueOf(type.getTypeId()));
		valueSet.addInsertValue("TypeFieldName", field.getFieldName());
		valueSet.addInsertValue("TypeFieldCategory", field.getFieldCategory().toString());
		//取得sql語法
		String query = valueSet.getInsertQuery();
		//execute
		getControl().execute(query, true);
		
		int typeFieldID = Integer.parseInt(getControl().getKeys()[0]);
		
		return typeFieldID;
	}
	
	/**
	 * 新增combo的資料(ComboFiele:WorkItem Priority欄位)
	 * @param typeFieldID
	 * @param comboName
	 */
	private void createDefaultComboFieldData(int typeFieldID, String comboName) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("eztrack_combofield");

		valueSet.addInsertValue("TypeFieldID", String.valueOf(typeFieldID));
		valueSet.addInsertValue("ComboName", comboName);

		String query = valueSet.getInsertQuery();
		try {
			getControl().execute(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}