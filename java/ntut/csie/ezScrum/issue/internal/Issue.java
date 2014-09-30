package ntut.csie.ezScrum.issue.internal;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.core.IIssueNote;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.RelationEnum;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.jcis.core.util.DateUtil;

import org.jdom.Element;


public class Issue implements IIssue {

	private String m_category = "";
	private String m_reproducibility = "";
	private String m_severity = "";
	private String m_priority = "";
	private String m_summary = "";
	private String m_description = "";
	private String m_additional = "";
	private String m_viewStatus = "";
	private String m_assignto = "";
	private String m_reporter = "";
	private String m_projectID = "";
	private String m_projectName = "";
	private String m_integrationID = "";
	private String m_builderID = "";

	private long m_issueID = -1;
	private String m_issueLink = "";
	private long m_submittedDate = new Date().getTime();
	private String m_status = "";
	private ArrayList<AttachFileObject> attachFile = new ArrayList<AttachFileObject>();
	private long m_workingDate = 0;
	private List<IIssueHistory> m_histories = new ArrayList<IIssueHistory>();
	private List<IIssueHistory> m_tagHistories = new ArrayList<IIssueHistory>();
	protected Element m_tagRoot = new Element(ScrumEnum.ROOT_TAG);

	// 對Story的自訂分類標籤
	private List<TagObject> m_tag = new ArrayList<TagObject>();

	private List<IIssueNote> m_notes = new ArrayList<IIssueNote>();
	
	// 儲存 Issue 客製化欄位(IssueTrac) ID+類型+名稱+數值
	private List<IssueTypeField> m_listField = new ArrayList<IssueTypeField>();
	// 儲存 Issue 客製化類型名稱
	private String m_categoryName = "";

	public Issue() {
	}

	public Date getSubmittedDateDate() {
		return new Date(this.m_submittedDate);
	}

	@Override
	public String getCategory() {
		return m_category;
	}

	@Override
	public void setCategory(String category) {
		m_category = category;
	}

	@Override
	public String getReproducibility() {
		return m_reproducibility;
	}

	@Override
	public void setReproducibility(String reproducibility) {
		m_reproducibility = reproducibility;
	}

	@Override
	public String getSeverity() {
		return m_severity;
	}

	@Override
	public void setSeverity(String severity) {
		m_severity = severity;
	}

	@Override
	public String getPriority() {
		return m_priority;
	}

	@Override
	public void setPriority(String priority) {
		m_priority = priority;
	}

	@Override
	public String getSummary() {
		return m_summary;
	}

	@Override
	public void setSummary(String summary) {
		m_summary = summary;
	}

	@Override
	public String getDescription() {
		return m_description;
	}

	@Override
	public void setDescription(String description) {
		m_description = description;
	}

	@Override
	public String getAdditional() {
		return m_additional;
	}

	@Override
	public void setAdditional(String additional) {
		m_additional = additional;
	}

	@Override
	public String getViewStatus() {
		return m_viewStatus;
	}

	@Override
	public void setViewState(String viewStatus) {
		m_viewStatus = viewStatus;
	}

	@Override
	public String getAssignto() {
		return m_assignto;
	}

	@Override
	public void setAssignto(String assignto) {
		m_assignto = assignto;
	}

	@Override
	public String getProjectID() {
		return m_projectID;
	}

	@Override
	public void setProjectID(String projectID) {
		m_projectID = projectID;
	}

	@Override
	public String getProjectName() {
		return m_projectName;
	}

	@Override
	public void setProjectName(String projectName) {
		this.m_projectName = projectName;
	}
	
	
	@Override
	public String getIntegrationID() {
		return m_integrationID;
	}

	@Override
	public void setIntegrationID(String integrationID) {
		m_integrationID = integrationID;
	}

	@Override
	public String getBuilderID() {
		return m_builderID;
	}

	@Override
	public void setBuilderID(String builderID) {
		m_builderID = builderID;
	}

	@Override
	public String getStatus() {
		return m_status;
	}

	@Override
	public long getSubmittedDate() {
		return m_submittedDate;
	}

	@Override
	public void setStatus(String status) {
		m_status = status;
	}

	@Override
	public void setSubmittedDate(long submitted) {
		m_submittedDate = submitted;
	}

	@Override
	public String getIssueLink() {
		return m_issueLink;
	}

	@Override
	public void setIssueLink(String issueLink) {
		m_issueLink = issueLink;
	}

	@Override
	public long getIssueID() {
		return m_issueID;
	}

	@Override
	public void setIssueID(long issueID) {
		m_issueID = issueID;
	}

	@Override
	public void setIssue(IIssue issue) {
		m_category = issue.getCategory();
		m_reproducibility = issue.getReproducibility();
		m_severity = issue.getSeverity();
		m_priority = issue.getPriority();
		m_summary = issue.getSummary();
		m_description = issue.getDescription();
		m_additional = issue.getAdditional();
		m_viewStatus = issue.getViewStatus();
		m_assignto = issue.getAssignto();

		m_issueID = issue.getIssueID();
		m_status = issue.getStatus();
		m_submittedDate = issue.getSubmittedDate();
		m_issueLink = issue.getIssueLink();
		attachFile = issue.getAttachFile();
		m_tag = issue.getTags();

		this.m_histories = issue.getHistory();
		this.setTagContent(issue.getTagContentRoot());
	}

	@Override
	public Date getStatusUpdated(int status) {
		return getStatusUpdated(new Date(), status);
	}

	@Override
	public Date getStatusUpdated(Date date, int status) {
		Date result = null;
		int lastStatus = 0;
		for (IIssueHistory history : m_histories) {
			String fieldName = history.getFieldName();
			if (fieldName!=null && fieldName.equals(IIssueHistory.STATUS_FIELD_NAME)) {
				if (history.getModifyDate() <= date.getTime()) {
					lastStatus = Integer.parseInt(history.getNewValue());
					if (lastStatus >= status)
						result = new Date(history.getModifyDate());
				} else
					break;
			}
		}

		if (lastStatus < status)
			return null;

		return result;
	}

	@Override
	public long getWorkingUpdated() {
		return this.m_workingDate;
	}

	@Override
	public void setWorkingUpdated(long workingDate) {
		this.m_workingDate = workingDate;
	}

	@Override
	public Element getTagContentRoot() {
		return m_tagRoot;
	}

	@Override
	public void setTagContent(Element historyRoot) {
		m_tagRoot = historyRoot;
		generateTagReleationHistory();
	}

	private List<IIssueHistory> getstatusHistory() {
		List<IIssueHistory> list = new ArrayList<IIssueHistory>();		
		for (IIssueHistory his : m_histories) {
			if ( his.getFieldName()!= null && his.getFieldName().compareTo("status") == 0)// history
				list.add(his);
		}
		/*
		 * //做一個極小值 IIssueHistory min=new IssueHistory(); min=list.get(0);
		 * min.setModifyDate(1); min.setOldValue(min.getNewValue());
		 * list.add(0,min);
		 */
		// 值一個極大值
		if (list.size() != 0) {
			IIssueHistory max = new IssueHistory();
			max.setFieldName(list.get(list.size() - 1).getFieldName());
			max.setModifyDate(Long.MAX_VALUE);
			max.setNewValue(list.get(list.size() - 1).getNewValue());
			max.setOldValue(list.get(list.size() - 1).getNewValue());
			list.add(max);
		}
		return list;
	}

	public int DateStatus(Date date)// 傳入想知道那一天的狀態,ex 07/28的issue 狀態為ASSIGNED
									// 則回傳ITSEUUM.ASSIGN_STATUS
	{

		long dateTime = date.getTime();
		List<IIssueHistory> templist = getstatusHistory();
		if (templist.size() != 0) {
			for (int i = 0; i < templist.size(); i++) {
				IIssueHistory his = templist.get(i);
				if (dateTime < his.getModifyDate()) {
					int temp = Integer.parseInt(his.getOldValue());
					switch (temp) {
					case 10:
						return ITSEnum.NEW_STATUS;
					case 50:
						return ITSEnum.ASSIGNED_STATUS;
					case 90:
						return ITSEnum.CLOSED_STATUS;
					}
				}
			}
		}
		return 10;
	}

	private void generateTagReleationHistory() {
		@SuppressWarnings("unchecked")
		List<Element> tags = m_tagRoot.getChildren();
		HashMap<String, Map<Date, String>> map = new HashMap<String, Map<Date, String>>();
		// xpath:/root/JCIS/Iteration/10
		// xpath:/root/Tag/ChildTag/Text
		for (Element tag : tags) {
			@SuppressWarnings("unchecked")
			List<Element> childTags = tag.getChildren();
			for (Element childTag : childTags) {
				Map<Date, String> childMap = map.get(childTag.getName());
				if (childMap == null)
					childMap = new TreeMap<Date, String>();
				childMap.put(DateUtil.dayFillter(tag.getAttributeValue("id"),
						DateUtil._16DIGIT_DATE_TIME_2), childTag.getText());
				map.put(childTag.getName(), childMap);
			}
		}

		for (String fieldName : map.keySet()) {
			Map<Date, String> childMap = map.get(fieldName);
			Double previous = 0.0;
			for (Date id : childMap.keySet()) {
				String currentStr = childMap.get(id);
				try {
					Double current = Double.parseDouble(currentStr);
					if (current == previous)
						continue;
					IIssueHistory history = new IssueHistory();
					history.setIssueID(this.m_issueID);
					history.setHistoryID(Long.parseLong(DateUtil.format(id,
							DateUtil._16DIGIT_DATE_TIME_2)));
					history.setModifyDate(id.getTime());
					history.setFieldName(fieldName);
					NumberFormat formater = NumberFormat.getInstance();
					history.setOldValue(formater.format(previous));
					history.setNewValue(formater.format(current));
					history.setType(IIssueHistory.OTHER_TYPE);
					this.m_tagHistories.add(history);
					previous = current;
				} catch (Exception e) {
					break;
				}
			}

		}
	}

	@Override
	public void addTagValue(Element element) {
		@SuppressWarnings("unchecked")
		List<Element> subElems = element.getChildren();
		ArrayList<String> removeList = new ArrayList<String>();

		for (Element subElem : subElems) {
			String elementName = subElem.getName();
			if (isSameValueBefore(subElem))
				removeList.add(elementName);
		}

		for (String name : removeList)
			element.removeChild(name);

		if (element.getChildren().size() > 0)
			this.m_tagRoot.addContent(element);
	}

	private boolean isSameValueBefore(Element subElem) {
		String value = getTagValue(subElem.getName());
		String current = subElem.getText();
		if (value == null && (current != null || !current.equals("")))
			return false;
		if (value.equals(subElem.getText()))
			return true;
		return false;
	}

	@Deprecated
	@Override
	public String getTagValue(String name, Date date) {
		Date lastDate = null;
		String lastValue = null;

		if (m_tagRoot == null)
			return lastValue;
		@SuppressWarnings("unchecked")
		List<Element> historyList = this.m_tagRoot.getChildren();
		for (Element history : historyList) {
			String value = history.getChildText(name);
			Date modifyDate = DateUtil.dayFillter(history.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR),DateUtil._16DIGIT_DATE_TIME_2);
			if (value != null&& date.getTime() >= DateUtil.dayFilter(modifyDate).getTime()) {
				if (lastDate == null) {
					lastDate = modifyDate;
					lastValue = value;
					continue;
				}
				if (modifyDate.getTime() > lastDate.getTime()) {
					lastDate = modifyDate;
					lastValue = value;
					continue;
				}
			}
		}
		return lastValue;
	}

	// 只取最新的結果
	@Deprecated
	@Override
	public String getTagValue(String name) {
		return getTagValue(name, new Date());
	}

	// 回傳所有此Tag的Value，並且每個Tag建立的時間附帶時間
	public Map<Date, String> getTagValueList(String name) {
		TreeMap<Date, String> map = new TreeMap<Date, String>();
		List<Element> historyList = this.m_tagRoot.getChildren();
		for (Element history : historyList) {
			String value = history.getChildText(name);
			Date modifyDate = DateUtil.dayFillter(history.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR), DateUtil._16DIGIT_DATE_TIME_2);
			if(value!=null){
				map.put(modifyDate, value);
			}
		}

		return map;
	}

	public String getImportance() {
		String value = getTagValue(ScrumEnum.IMPORTANCE);
		if (value == null)
			return ScrumEnum.DIGITAL_BLANK_VALUE;
		return value;
	}
	
	public String getValue() {
		String value = getTagValue(ScrumEnum.VALUE);
		if (value == null)
			return ScrumEnum.DIGITAL_BLANK_VALUE;
		return value;
	}

	public String getEstimated() {
		String value = getTagValue(ScrumEnum.ESTIMATION);
		if (value == null)
			return ScrumEnum.DIGITAL_BLANK_VALUE;
		return value;
	}

	public String getRemains() {
		String value = getTagValue(ScrumEnum.REMAINS);
		if (value == null)
			return getEstimated();
		return value;
	}

	public String getSprintID() {
		String temp = getTagValue(ScrumEnum.SPRINT_ID);
		if (temp == null || temp.isEmpty())
			return "-1";
		else
			return temp;
	}

	public String getReleaseID() {
		String value = getTagValue(ScrumEnum.RELEASE_TAG);
		if (value == null)
			return "-1";
		return value;
	}

	public String getHowToDemo() {
		String value = getTagValue(ScrumEnum.HOWTODEMO);
		if (value == null)
			return ScrumEnum.STRING_BLANK_VALUE;
		return value;
	}

	public String getNotes() {
		String value = getTagValue(ScrumEnum.NOTES);
		if (value == null)
			return ScrumEnum.STRING_BLANK_VALUE;
		return value;
	}

	@Deprecated
	@Override
	public String getPartners() {
		String value = getTagValue(ScrumEnum.PARTNERS);
		if (value == null)
			return ScrumEnum.STRING_BLANK_VALUE;
		return value;
	}

	public String getActualHour() {
		String value = getTagValue(ScrumEnum.ACTUALHOUR);
		if (value == null)
			return ScrumEnum.DIGITAL_BLANK_VALUE;
		return value;
	}

	@Deprecated
	@Override
	public List<Long> getChildrenID() {
		// return m_children;
		return this.getChildrenID(new Date());
	}

	@Deprecated
	@Override
	public List<Long> getParentsID() {
		// return m_parnets;
		return this.getParentsID(new Date());
	}

	@Override
	public int getStatusValue() {
		return ITSEnum.getStatus(this.getStatus());
	}

	@Override
	public Date getLastUpdate() {
		Date lastUpdate = null;
		for (IIssueHistory history : this.m_histories) {
			//if (history.getFieldName().equals(IIssueHistory.STATUS_FIELD_NAME)) {
				if (lastUpdate == null)
					lastUpdate = new Date(history.getModifyDate());
				else if (lastUpdate.getTime() < history.getModifyDate())
					lastUpdate = new Date(history.getModifyDate());
			//}
		}
		return lastUpdate;
	}

	@Override
	public void addIssueHistory(IIssueHistory history) {
		this.m_histories.add(history);

	}

	@Override
	public List<IIssueHistory> getIssueHistories() {
		ArrayList<IIssueHistory> list = new ArrayList<IIssueHistory>();
		// 有兩種history,一種為issue的,一種為Scrum Tag的
		// issue
		for (IIssueHistory history : this.m_histories) {
			int index = 0;
			for (IIssueHistory item : list) {
				if (item.getModifyDate() > history.getModifyDate())
					break;
				index++;
			}
			list.add(index, history);
		}

		// Scrum Tag : 這部份要抽出至Story及Task中實作
		for (IIssueHistory history : this.m_tagHistories) {
			int index = 0;
			for (IIssueHistory item : list) {
				if (item.getModifyDate() > history.getModifyDate())
					break;
				index++;
			}
			list.add(index, history);
		}
		return list;
	}

	@Deprecated
	@Override
	public List<Long> getChildrenID(Date date) {
		ArrayList<Long> list = new ArrayList<Long>();
		for (IIssueHistory history : this.m_histories) {
			if (history.getModifyDate() > date.getTime())
				continue;
			if (history.getType() == IIssueHistory.RELEATIONSHIP_ADD_TYPE
					&& history.getOldValue().equals(
							IIssueHistory.PARENT_OLD_VALUE))
				list.add(Long.parseLong(history.getNewValue()));
			else if (history.getType() == IIssueHistory.RELEATIONSHIP_DELETE_TYPE
					&& history.getOldValue().equals(
							IIssueHistory.PARENT_OLD_VALUE))
				list.remove(Long.parseLong(history.getNewValue()));
		}
		return list;
	}

	@Deprecated
	@Override
	public List<Long> getParentsID(Date date) {
		ArrayList<Long> list = new ArrayList<Long>();
		for (IIssueHistory history : this.m_histories) {
			if (history.getModifyDate() > date.getTime())
				break;
			if (history.getType() == IIssueHistory.RELEATIONSHIP_ADD_TYPE
					&& history.getOldValue().equals(
							IIssueHistory.CHILD_OLD_VALUE))
				list.add(Long.parseLong(history.getNewValue()));
			else if (history.getType() == IIssueHistory.RELEATIONSHIP_DELETE_TYPE
					&& history.getOldValue().equals(
							IIssueHistory.CHILD_OLD_VALUE)) {
				list.remove(Long.parseLong(history.getNewValue()));
			}
		}
		return list;
	}

	public List<IIssueHistory> getHistory() {
		return this.m_histories;
	}

	public void setHistory(List<IIssueHistory> history) {
		this.m_histories = history;
	}
	
	/* 抓取Relation的關係 */
	/* 原本應該建立一個relationship的service來抓取資料
	 * 但起先Story跟task的關係使用history來取得，故此處也先以history來抓取資料
	 * */
	public List<IssueRelationship> getRelationships() {
		//有關relation新增的歷史資料
		List<IIssueHistory> addRelations = new ArrayList<IIssueHistory>();
		for(IIssueHistory history: m_histories){
			if(history.getType()==RelationEnum.RELEATIONSHIP_ADD_TYPE)
				addRelations.add(history);
				
		}
		//relation刪除的歷史資料
		List<IIssueHistory> delRelations = new ArrayList<IIssueHistory>();
		for(IIssueHistory history: m_histories){
			if(history.getType()==RelationEnum.RELEATIONSHIP_DELETE_TYPE)
				delRelations.add(history);
		}		
	
		//兩者相減則可得到目前有關issue的relation資訊
		for(IIssueHistory history: delRelations){
			for(IIssueHistory history2: addRelations){
				if(history.getNewValue().equals(history2.getNewValue())) {
					addRelations.remove(history2);
					break;
				}
			}
		}
		
		List<IssueRelationship> relations = new ArrayList<IssueRelationship>();
		// 將history的資訊塞入至issue relation這個物件
		for(IIssueHistory history: addRelations){
			long m_issueID = history.getIssueID();
			String r_issueID = history.getNewValue();
			String type = history.getOldValue();
			IssueRelationship re = new IssueRelationship(m_issueID);
			re.setRelationIssueID(Long.valueOf(r_issueID));
			
			if(type.equals(RelationEnum.PARENT_OLD_VALUE))
				re.setRelationType(RelationEnum.PARENT_OF);
			else if(type.equals(RelationEnum.CHILD_OLD_VALUE))
				re.setRelationType(RelationEnum.CHILD_OF);
			else if(type.equals(RelationEnum.IMPLICATIONOF_OLD_VALUE))
				re.setRelationType(RelationEnum.IMPLICATIONOF_OF);
			else if(type.equals(RelationEnum.TRANSFORMBY_OLD_VALUE))
				re.setRelationType(RelationEnum.TRANSFORMBY_BY);
			else if(type.equals(RelationEnum.TRANSFORMTO_OLD_VALUE))
				re.setRelationType(RelationEnum.TRANSFORMTO_TO);
			relations.add(re);
		}
		return relations;
	}

	public List<IIssueHistory> getTagHistory() {
		return this.m_tagHistories;
	}

	public void setTagHistory(List<IIssueHistory> history) {
		this.m_tagHistories = history;
	}

	@Override
	public void addIssueNote(IIssueNote note) {
		if (note.getIssueID() == 0)
			m_notes.add(note);
		else {
			for (int i = 0; i < m_notes.size(); i++) {
				IIssueNote temp = m_notes.get(i);
				if (temp.getIssueID() == 0)
					continue;
				if (temp.getIssueID() == note.getIssueID()) {
					m_notes.add(i, note);
					break;
				}
			}
		}
	}

	@Override
	public void addIssueNote(String text) {
		IssueNote note = new IssueNote();
		note.setSubmittedDate(new Date().getTime());
		note.setModifiedDate(new Date().getTime());
		note.setText(text);
		m_notes.add(note);
	}

	@Override
	public List<IIssueNote> getIssueNotes() {
		return m_notes;
	}

	@Override
	public void setIssueNotes(List<IIssueNote> notes) {
		m_notes = notes;
	}

	@Override
	public long getAssignedDate() {
		long assignedDate = 0;
		for (IIssueHistory history : m_histories) {
			if (history.getOldValue().compareTo("10") == 0
					&& history.getNewValue().compareTo("50") == 0) {
				assignedDate = history.getModifyDate();
			}

		}

		return assignedDate;
	}

	@Override
	public long getCloseDate() {

		long closeDate = 0;
		for (IIssueHistory history : m_histories) {
			if (history.getOldValue().compareTo("50") == 0
					&& history.getNewValue().compareTo("90") == 0) {
				closeDate = history.getModifyDate();
			}

		}

		return closeDate;
	}

	@Override
	public String getCreateBy() {
		return this.m_reporter;
	}

	public String getReporter() {
		return m_reporter;
	}

	public void setReporter(String m_reporter) {
		this.m_reporter = m_reporter;
	}

	public ArrayList<AttachFileObject> getAttachFile() {
		return attachFile;
	}

	public void setAttachFile(ArrayList<AttachFileObject> FileList) {
		attachFile = FileList;
	}

	@Override
	public void addAttachFile(AttachFileObject attach) {
		this.attachFile.add(attach);
	}

	@Override
	public List<TagObject> getTags() {
		return m_tag;
	}

	@Override
	public void setTags(List<TagObject> tags) {
		this.m_tag = tags;
	}

	@Override
	public void addTag(TagObject tag) {
		this.m_tag.add(tag);
	}
	
	public void setCategoryName(String name) {
		this.m_categoryName = name;
	}
	
	public String getCategoryName() {
		return this.m_categoryName;
	}
	
	public void addField(IssueTypeField field)	{
		this.m_listField.add(field);
	}
	
	public String getFieldValue(String name)	{
		for (IssueTypeField field : this.m_listField){
			if (field.getFieldName().equals(name))
				return field.getFieldValue();
		}
		return "-1";
	}
	
	public IssueTypeField getField(String name)	{
		for (IssueTypeField field : this.m_listField){
			if (field.getFieldName().equals(name))
				return field;
		}
		return null;
	}
	
	public void setFieldValue(int id, String value)	{
		for (IssueTypeField field : this.m_listField){
			if (field.getFieldID() == id)
				field.setFieldValue(value);
		}
	}
	
	public void setFieldValue(String name, String value)	{
		for (IssueTypeField field : this.m_listField){
			if (field.getFieldName().equals(name))
				field.setFieldValue(value);
		}
	}
	
	public void setFields(List<IssueTypeField> fields) {
		this.m_listField = fields;
	}
	
	public List<IssueTypeField> getFields()	{
		return this.m_listField;
	}
}
