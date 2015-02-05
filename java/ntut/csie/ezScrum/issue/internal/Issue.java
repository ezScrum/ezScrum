package ntut.csie.ezScrum.issue.internal;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.issue.core.IIssueNote;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.databasEnum.IssueTypeEnum;
import ntut.csie.jcis.core.util.DateUtil;

import org.jdom.Element;

public class Issue implements IIssue {
	private String mCategory = "";
	private String mReproducibility = "";
	private String mSeverity = "";
	private String mPriority = "";
	private String mSummary = "";
	private String mDescription = "";
	private String mAdditional = "";
	private String mViewStatus = "";
	private String mAssignto = "";
	private String mReporter = "";
	private String mProjectId = "";
	private String mProjectName = "";
	private String mIntegrationId = "";
	private String mBuilderId = "";
	private String mIssueLink = "";
	private String mStatus = "";
	private long mId = -1;
	private long mSubmittedDate = new Date().getTime();
	private long mWorkingDate = 0;
	private long mParentId;
	
	private ArrayList<AttachFileObject> mAttachFiles = new ArrayList<AttachFileObject>();
	private ArrayList<HistoryObject> mHistories = new ArrayList<HistoryObject>();
	private ArrayList<IIssueHistory> mTagHistories = new ArrayList<IIssueHistory>();
	private ArrayList<Long> mChildrenId = new ArrayList<Long>();
	protected Element mTagRoot = new Element(ScrumEnum.ROOT_TAG);
	// 對 Story 的自訂分類標籤
	private List<TagObject> mTag = new ArrayList<TagObject>();
	private List<IIssueNote> mNotes = new ArrayList<IIssueNote>();

	// 儲存 Issue 客製化欄位(IssueTrac) ID+類型+名稱+數值
	private List<IssueTypeField> mListField = new ArrayList<IssueTypeField>();
	// 儲存 Issue 客製化類型名稱
	private String mCategoryName = "";

	public Issue() {}

	public Date getSubmittedDateDate() {
		return new Date(this.mSubmittedDate);
	}

	@Override
	public String getCategory() {
		return mCategory;
	}

	@Override
	public void setCategory(String category) {
		mCategory = category;
	}

	public int getIssueType() {
		if (mCategory.equals("Task")) {
			return IssueTypeEnum.TYPE_TASK;
		} else if (mCategory.equals("Story")) {
			return IssueTypeEnum.TYPE_STORY;
		} else if (mCategory.equals("UnplannedItem")) {
			return IssueTypeEnum.TYPE_UNPLANNED;
		} else if (mCategory.equals("Good") || mCategory.equals("Improvement")) {
			return IssueTypeEnum.TYPE_RETROSPECTIVE;
		} else {
			return 0;
		}
	}

	@Override
	public String getReproducibility() {
		return mReproducibility;
	}

	@Override
	public void setReproducibility(String reproducibility) {
		mReproducibility = reproducibility;
	}

	@Override
	public String getSeverity() {
		return mSeverity;
	}

	@Override
	public void setSeverity(String severity) {
		mSeverity = severity;
	}

	@Override
	public String getPriority() {
		return mPriority;
	}

	@Override
	public void setPriority(String priority) {
		mPriority = priority;
	}

	@Override
	public String getSummary() {
		return mSummary;
	}

	@Override
	public void setSummary(String summary) {
		mSummary = summary;
	}

	@Override
	public String getDescription() {
		return mDescription;
	}

	@Override
	public void setDescription(String description) {
		mDescription = description;
	}

	@Override
	public String getAdditional() {
		return mAdditional;
	}

	@Override
	public void setAdditional(String additional) {
		mAdditional = additional;
	}

	@Override
	public String getViewStatus() {
		return mViewStatus;
	}

	@Override
	public void setViewState(String viewStatus) {
		mViewStatus = viewStatus;
	}

	@Override
	public String getAssignto() {
		return mAssignto;
	}

	@Override
	public void setAssignto(String assignto) {
		mAssignto = assignto;
	}

	@Override
	public String getProjectID() {
		return mProjectId;
	}

	@Override
	public void setProjectID(String projectID) {
		mProjectId = projectID;
	}

	@Override
	public String getProjectName() {
		return mProjectName;
	}

	@Override
	public void setProjectName(String projectName) {
		this.mProjectName = projectName;
	}

	@Override
	public String getIntegrationId() {
		return mIntegrationId;
	}

	@Override
	public void setIntegrationID(String integrationID) {
		mIntegrationId = integrationID;
	}

	@Override
	public String getBuilderID() {
		return mBuilderId;
	}

	@Override
	public void setBuilderID(String builderID) {
		mBuilderId = builderID;
	}

	@Override
	public String getStatus() {
		return mStatus;
	}

	@Override
	public long getSubmittedDate() {
		return mSubmittedDate;
	}

	@Override
	public void setStatus(String status) {
		mStatus = status;
	}

	@Override
	public void setSubmittedDate(long submitted) {
		mSubmittedDate = submitted;
	}

	@Override
	public String getIssueLink() {
		return mIssueLink;
	}

	@Override
	public void setIssueLink(String issueLink) {
		mIssueLink = issueLink;
	}

	@Override
	public long getIssueID() {
		return mId;
	}

	@Override
	public void setIssueID(long issueId) {
		mId = issueId;
	}

	@Override
	public void setIssue(IIssue issue) throws SQLException {
		mCategory = issue.getCategory();
		mReproducibility = issue.getReproducibility();
		mSeverity = issue.getSeverity();
		mPriority = issue.getPriority();
		mSummary = issue.getSummary();
		mDescription = issue.getDescription();
		mAdditional = issue.getAdditional();
		mViewStatus = issue.getViewStatus();
		mAssignto = issue.getAssignto();

		mId = issue.getIssueID();
		mStatus = issue.getStatus();
		mSubmittedDate = issue.getSubmittedDate();
		mIssueLink = issue.getIssueLink();
		mAttachFiles = issue.getAttachFiles();
		mTag = issue.getTags();

		mHistories = issue.getHistories();
		mChildrenId = issue.getChildrenId();
		
		setTagContent(issue.getTagContentRoot());
	}

	@Override
	public Date getStatusUpdated(int status) {
		return getStatusUpdated(new Date(), status);
	}

	@Override
	public Date getStatusUpdated(Date date, int status) {
		Date result = null;
		int lastStatus = 0;
		try {
			for (HistoryObject history : getHistories()) {
				if (history.getHistoryType() == HistoryObject.TYPE_STATUS) {
					if (history.getCreateTime() <= date.getTime()) {
						lastStatus = Integer.parseInt(history.getNewValue());
						if (lastStatus >= status) {
							result = new Date(history.getCreateTime());
						} else {
							break;
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (lastStatus < status) {
			return null;
		}
		return result;
	}

	@Override
	public long getWorkingUpdated() {
		return mWorkingDate;
	}

	@Override
	public void setWorkingUpdated(long workingDate) {
		mWorkingDate = workingDate;
	}

	@Override
	public Element getTagContentRoot() {
		return mTagRoot;
	}

	@Override
	public void setTagContent(Element historyRoot) {
		mTagRoot = historyRoot;
		generateTagReleationHistory();
	}

	private ArrayList<HistoryObject> getStatusHistories() {
		ArrayList<HistoryObject> histories = new ArrayList<HistoryObject>();
		try {
			for (HistoryObject history : getHistories()) {
				if (history.getHistoryType() == HistoryObject.TYPE_STATUS) {
					histories.add(history);
				}
			}
		} catch (SQLException e) {
		}
		return histories;
	}

	// 傳入想知道那一天的狀態,ex 07/28的issue 狀態為 ASSIGNED
	// 則回傳ITSEUUM.ASSIGN_STATUS
	public int getDateStatus(Date date) {
		long dateTime = date.getTime();
		
		int status = ITSEnum.NEW_STATUS;
		ArrayList<HistoryObject> statusHistories = getStatusHistories();
		if (statusHistories.size() != 0) {
			for (int i = 0; i < statusHistories.size(); i++) {
				HistoryObject history = statusHistories.get(i);
				if (history.getCreateTime() <= dateTime) {
					status = Integer.parseInt(history.getNewValue());
				}
			}
		}
		return status;
	}
	
	private void generateTagReleationHistory() {
		@SuppressWarnings("unchecked")
		List<Element> tags = mTagRoot.getChildren();
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
					history.setIssueID(mId);
					history.setHistoryID(Long.parseLong(DateUtil.format(id,
							DateUtil._16DIGIT_DATE_TIME_2)));
					history.setModifyDate(id.getTime());
					history.setFieldName(fieldName);
					NumberFormat formater = NumberFormat.getInstance();
					history.setOldValue(formater.format(previous));
					history.setNewValue(formater.format(current));
					history.setType(IIssueHistory.OTHER_TYPE);
					mTagHistories.add(history);
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
			if (isSameValueBefore(subElem)) {
				removeList.add(elementName);
			}
		}

		for (String name : removeList) {
			element.removeChild(name);
		}

		if (element.getChildren().size() > 0) {
			mTagRoot.addContent(element);
		}
	}

	private boolean isSameValueBefore(Element subElem) {
		String value = getTagValue(subElem.getName());
		String current = subElem.getText();
		if (value == null && (current != null || !current.equals(""))) return false;
		if (value.equals(subElem.getText())) return true;
		return false;
	}

	@Deprecated
	@Override
	public String getTagValue(String name, Date date) {
		Date lastDate = null;
		String lastValue = null;

		if (mTagRoot == null) return lastValue;
		@SuppressWarnings("unchecked")
		List<Element> historyList = mTagRoot.getChildren();
		for (Element history : historyList) {
			String value = history.getChildText(name);
			Date modifyDate = DateUtil.dayFillter(history.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR), DateUtil._16DIGIT_DATE_TIME_2);
			if (value != null && date.getTime() >= DateUtil.dayFilter(modifyDate).getTime()) {
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
		List<Element> historyList = mTagRoot.getChildren();
		for (Element history : historyList) {
			String value = history.getChildText(name);
			Date modifyDate = DateUtil.dayFillter(history.getAttributeValue(ScrumEnum.ID_HISTORY_ATTR), DateUtil._16DIGIT_DATE_TIME_2);
			if (value != null) {
				map.put(modifyDate, value);
			}
		}
		return map;
	}

	public String getImportance() {
		String value = getTagValue(ScrumEnum.IMPORTANCE);
		if (value == null) return ScrumEnum.DIGITAL_BLANK_VALUE;
		return value;
	}

	public String getValue() {
		String value = getTagValue(ScrumEnum.VALUE);
		if (value == null) return ScrumEnum.DIGITAL_BLANK_VALUE;
		return value;
	}

	public String getEstimated() {
		String value = getTagValue(ScrumEnum.ESTIMATION);
		if (value == null) return ScrumEnum.DIGITAL_BLANK_VALUE;
		return value;
	}

	public String getRemains() {
		String value = getTagValue(ScrumEnum.REMAINS);
		if (value == null) return getEstimated();
		return value;
	}

	public String getSprintID() {
		String temp = getTagValue(ScrumEnum.SPRINT_ID);
		if (temp == null || temp.isEmpty()) return "-1";
		else return temp;
	}

	public String getReleaseID() {
		String value = getTagValue(ScrumEnum.RELEASE_TAG);
		if (value == null) return "-1";
		return value;
	}

	public String getHowToDemo() {
		String value = getTagValue(ScrumEnum.HOWTODEMO);
		if (value == null) return ScrumEnum.STRING_BLANK_VALUE;
		return value;
	}

	public String getNotes() {
		String value = getTagValue(ScrumEnum.NOTES);
		if (value == null) return ScrumEnum.STRING_BLANK_VALUE;
		return value;
	}

	@Deprecated
	@Override
	public String getPartners() {
		String value = getTagValue(ScrumEnum.PARTNERS);
		if (value == null) return ScrumEnum.STRING_BLANK_VALUE;
		return value;
	}

	public String getActualHour() {
		String value = getTagValue(ScrumEnum.ACTUALHOUR);
		if (value == null) return ScrumEnum.DIGITAL_BLANK_VALUE;
		return value;
	}

	@Override
	public int getStatusValue() {
		return ITSEnum.getStatus(this.getStatus());
	}

	@Override
	public Date getLastUpdate() {
		Date lastUpdate = null;
		try {
			for (HistoryObject history : getHistories()) {
				if (lastUpdate == null) {
					lastUpdate = new Date(history.getCreateTime());
				} else if (lastUpdate.getTime() < history.getCreateTime()) {
					lastUpdate = new Date(history.getCreateTime());
				}
			}			
		} catch (SQLException e) {
		}
		return lastUpdate;
	}

	public void setChildrenId(ArrayList<Long> childrenId) {
		mChildrenId = childrenId;
	}
	
	public ArrayList<Long> getChildrenId() {
		return mChildrenId;
	}
	
	public void setParentId(long parentId) {
		mParentId = parentId;
	}
	
	public long getParentId() {
		return mParentId;
	}
	
	/**
	 * for ezScrum v1.8 temp function
	 */
	public void addHistory(HistoryObject history) {
		mHistories.add(history);
	}
	
	/**
	 * for ezScrum v1.8 temp function
	 */
	public ArrayList<HistoryObject> getHistories() throws SQLException {
		mHistories = HistoryDAO.getInstance().getHistoriesByIssue(mId, getIssueType());
		return mHistories;
	}

	/**
	 * for ezScrum v1.8 temp function
	 */
	public void setHistories(ArrayList<HistoryObject> histories) {
		mHistories = histories;
	}

//	/* 抓取Relation的關係
//	 * 原本應該建立一個relationship的service來抓取資料 但起先Story跟task的關係使用history來取得，故此處也先以history來抓取資料
//	 */
//	public List<IssueRelationship> getRelationships() {
//		//有關relation新增的歷史資料
//		List<IIssueHistory> addRelations = new ArrayList<IIssueHistory>();
//		for(IIssueHistory history: mIHistories){
//			if(history.getType()==RelationEnum.RELEATIONSHIP_ADD_TYPE)
//				addRelations.add(history);
//		}
//		//relation刪除的歷史資料
//		List<IIssueHistory> delRelations = new ArrayList<IIssueHistory>();
//		for(IIssueHistory history: mIHistories){
//			if(history.getType()==RelationEnum.RELEATIONSHIP_DELETE_TYPE)
//				delRelations.add(history);
//		}		
//	
//		//兩者相減則可得到目前有關issue的relation資訊
//		for(IIssueHistory history: delRelations){
//			for(IIssueHistory history2: addRelations){
//				if(history.getNewValue().equals(history2.getNewValue())) {
//					addRelations.remove(history2);
//					break;
//				}
//			}
//		}
//		
//		List<IssueRelationship> relations = new ArrayList<IssueRelationship>();
//		// 將history的資訊塞入至issue relation這個物件
//		for(IIssueHistory history: addRelations){
//			long issueId = history.getIssueID();
//			String r_issueID = history.getNewValue();
//			String type = history.getOldValue();
//			IssueRelationship re = new IssueRelationship(issueId);
//			re.setRelationIssueID(Long.valueOf(r_issueID));
//			
//			if(type.equals(RelationEnum.PARENT_OLD_VALUE))
//				re.setRelationType(RelationEnum.PARENT_OF);
//			else if(type.equals(RelationEnum.CHILD_OLD_VALUE))
//				re.setRelationType(RelationEnum.CHILD_OF);
//			else if(type.equals(RelationEnum.IMPLICATIONOF_OLD_VALUE))
//				re.setRelationType(RelationEnum.IMPLICATIONOF_OF);
//			else if(type.equals(RelationEnum.TRANSFORMBY_OLD_VALUE))
//				re.setRelationType(RelationEnum.TRANSFORMBY_BY);
//			else if(type.equals(RelationEnum.TRANSFORMTO_OLD_VALUE))
//				re.setRelationType(RelationEnum.TRANSFORMTO_TO);
//			relations.add(re);
//		}
//		return relations;
//	}

//	public List<IIssueHistory> getTagHistory() {
//		return mTagHistories;
//	}

//	public void setTagHistory(ArrayList<IIssueHistory> history) {
//		mTagHistories = history;
//	}

	@Override
	public void addIssueNote(IIssueNote note) {
		if (note.getIssueID() == 0) mNotes.add(note);
		else {
			for (int i = 0; i < mNotes.size(); i++) {
				IIssueNote temp = mNotes.get(i);
				if (temp.getIssueID() == 0) continue;
				if (temp.getIssueID() == note.getIssueID()) {
					mNotes.add(i, note);
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
		mNotes.add(note);
	}

	@Override
	public List<IIssueNote> getIssueNotes() {
		return mNotes;
	}

	@Override
	public void setIssueNotes(List<IIssueNote> notes) {
		mNotes = notes;
	}

	@Override
	public long getCheckOutDate() {
		long checkOutDate = 0;
		try {
			for (HistoryObject history : getHistories()) {
				if (Integer.parseInt(history.getOldValue()) == ITSEnum.NEW_STATUS &&
					Integer.parseInt(history.getNewValue()) == ITSEnum.ASSIGNED_STATUS) {
					checkOutDate = history.getCreateTime();
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return checkOutDate;
	}

	@Override
	public long getDoneDate() {
		long closeDate = 0;
		try {
			for (HistoryObject history : getHistories()) {
				if (Integer.parseInt(history.getOldValue()) == ITSEnum.ASSIGNED_STATUS &&
					Integer.parseInt(history.getNewValue()) == ITSEnum.CLOSED_STATUS) {
					closeDate = history.getCreateTime();
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return closeDate;
	}

	@Override
	public String getCreateBy() {
		return mReporter;
	}

	public String getReporter() {
		return mReporter;
	}

	public void setReporter(String m_reporter) {
		mReporter = m_reporter;
	}

	public ArrayList<AttachFileObject> getAttachFiles() {
		return mAttachFiles;
	}

	public void setAttachFiles(ArrayList<AttachFileObject> FileList) {
		mAttachFiles = FileList;
	}

	@Override
	public void addAttachFile(AttachFileObject attach) {
		mAttachFiles.add(attach);
	}

	@Override
	public List<TagObject> getTags() {
		return mTag;
	}

	@Override
	public void setTags(List<TagObject> tags) {
		mTag = tags;
	}

	@Override
	public void addTag(TagObject tag) {
		mTag.add(tag);
	}

	public void setCategoryName(String name) {
		mCategoryName = name;
	}

	public String getCategoryName() {
		return mCategoryName;
	}

	public void addField(IssueTypeField field) {
		mListField.add(field);
	}

	public String getFieldValue(String name) {
		for (IssueTypeField field : mListField) {
			if (field.getFieldName().equals(name)) return field.getFieldValue();
		}
		return "-1";
	}

	public IssueTypeField getField(String name) {
		for (IssueTypeField field : mListField) {
			if (field.getFieldName().equals(name)) return field;
		}
		return null;
	}

	public void setFieldValue(int id, String value) {
		for (IssueTypeField field : mListField) {
			if (field.getFieldID() == id) field.setFieldValue(value);
		}
	}

	public void setFieldValue(String name, String value) {
		for (IssueTypeField field : mListField) {
			if (field.getFieldName().equals(name)) field.setFieldValue(value);
		}
	}

	public void setFields(List<IssueTypeField> fields) {
		mListField = fields;
	}

	public List<IssueTypeField> getFields() {
		return mListField;
	}

	@Override
	public long getAssignedDate() {
		try {
			for (HistoryObject history : getHistories()) {
				if (history.getHistoryType() == HistoryObject.TYPE_STATUS && 
					history.getNewValue().equals(String.valueOf(ITSEnum.ASSIGNED_STATUS)) &&
					history.getOldValue().equals(String.valueOf(ITSEnum.NEW_STATUS))) {
					return history.getCreateTime();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public long getCloseDate() {
		try {
			for (HistoryObject history : getHistories()) {
				if (history.getHistoryType() == HistoryObject.TYPE_STATUS && 
					history.getNewValue().equals(String.valueOf(ITSEnum.CLOSED_STATUS)) &&
					history.getOldValue().equals(String.valueOf(ITSEnum.ASSIGNED_STATUS))) {
					return history.getCreateTime();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
