package ntut.csie.ezScrum.issue.sql.service.internal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueNote;
import ntut.csie.ezScrum.issue.internal.IssueNote;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IQueryValueSet;
import ntut.csie.ezScrum.issue.sql.service.tool.ISQLControl;
import ntut.csie.ezScrum.iteration.support.TranslateSpecialChar;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.core.util.XmlFileUtil;

import org.jdom.Element;

public class MantisNoteService extends AbstractMantisService {

	public MantisNoteService(ISQLControl control, Configuration config) {
		setControl(control);
		setConfig(config);
	}

	public List<IIssueNote> getIssueNotes(IIssue issue) {
		List<IIssueNote> notes = new ArrayList<IIssueNote>();
		IQueryValueSet valueSet = new MySQLQuerySet();
		
		valueSet.addTableName("mantis_bugnote_table");
		valueSet.addTableName("mantis_bugnote_text_table");
		valueSet.addFieldEqualCondition("mantis_bugnote_text_table.id",
				"mantis_bugnote_table.bugnote_text_id");
		valueSet.addFieldEqualCondition("mantis_bugnote_table.bug_id", Long
				.toString(issue.getIssueID()));
		String query = valueSet.getSelectQuery();
		ResultSet result = getControl().executeQuery(query);
		
		try {
			while (result.next()) {
				IIssueNote note = new IssueNote();
				note.setIssueID(issue.getIssueID());
				note.setNoteID(result.getLong("mantis_bugnote_text_table.id"));
				note.setText(result.getString("note"));
				note.setSubmittedDate(result.getTimestamp("date_submitted").getTime());
				note.setModifiedDate(result.getTimestamp("last_modified").getTime());
				note.setHandler(getUserName(result.getInt("reporter_id")));
				notes.add(note);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return notes;
	}

	public void updateBugNote(IIssue issue) {
		TranslateSpecialChar translateChar = new TranslateSpecialChar();
		
		@SuppressWarnings("unchecked")
		String updateContext = XmlFileUtil.getXmlString(issue.getTagContentRoot().getChildren());
		updateContext = translateChar.TranslateDBChar(updateContext);
		// String query = "SELECT date_submitted, note,
		// mantis_bugnote_text_table.id FROM `mantis_bugnote_table` ,
		// `mantis_bugnote_text_table` WHERE mantis_bugnote_text_table.id =
		// mantis_bugnote_table.bugnote_text_id AND mantis_bugnote_table.bug_id
		// ="
		// + issue.getIssueID();

		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bugnote_table");
		valueSet.addTableName("mantis_bugnote_text_table");
		valueSet.addFieldEqualCondition("mantis_bugnote_text_table.id",
				"mantis_bugnote_table.bugnote_text_id");
		valueSet.addFieldEqualCondition("mantis_bugnote_table.bug_id", Long
				.toString(issue.getIssueID()));
		String query = valueSet.getSelectQuery();
//		System.out.println(query);
		try {
			ResultSet result = getControl().executeQuery(query);
				
			boolean exist = false;
			long noteID = 0;
			// 首先先找出做為tag的bug note
			while (result.next()) {
				noteID = result.getLong("mantis_bugnote_text_table.id");
				String note = result.getString("note");
				
				// 取得符合<JCIS>.....</JCIS>的字串
				if (note.contains("<JCIS") && !note.contains("<JCIS:")) {
					// 找到有符合的,便執行update的query
					// String updateQuery = "UPDATE `mantis_bugnote_text_table`
					// SET
					// `note` = '"
					// + updateContext
					// + "' WHERE `mantis_bugnote_text_table`.`id` ="
					// + noteID;
					valueSet.clear();
					valueSet.addTableName("mantis_bugnote_text_table");
					valueSet.addInsertValue("note", updateContext);
					valueSet.addEqualCondition("id", Long.toString(noteID));
					query = valueSet.getUpdateQuery();
					getControl().execute(query);

					exist = true;
					break;
				}
			}

			// 如果原先的issue中的bug note沒有記載任何tag時,便新增一個tag
			// 為了要解決mantis connect無法加入中文的問題
			// 所以先利用mantis connect產生空白的note
			// 然後再直接修改DB的資料
			if (!exist){
				insertBugNote(issue.getIssueID(), updateContext);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateIssueNote(IIssue issue, IIssueNote note) {
		List<IIssueNote> list = issue.getIssueNotes();
		if (note.getNoteID() > 0) {
			for (IIssueNote issueNote : list) {
				// String query = "SELECT date_submitted, note,
				// mantis_bugnote_text_table.id FROM `mantis_bugnote_table`
				// ,
				// `mantis_bugnote_text_table` WHERE
				// mantis_bugnote_text_table.id =
				// mantis_bugnote_table.bugnote_text_id AND
				// mantis_bugnote_table.bug_id
				// ="
				// + issue.getIssueID();
				if (issueNote.getIssueID() == note.getIssueID()) {
					// select the id, note in bugnote_text table, where bugnote_text.id = bugnote.id
					IQueryValueSet valueSet = new MySQLQuerySet();
					valueSet.addTableName("mantis_bugnote_text_table");
					valueSet.addResultRow("mantis_bugnote_text_table.id");
					valueSet.addResultRow("mantis_bugnote_text_table.note");
					valueSet.addLeftJoin("mantis_bugnote_table", "mantis_bugnote_table.bugnote_text_id", "mantis_bugnote_text_table.id");
					valueSet.addEqualCondition("mantis_bugnote_table.bugnote_text_id", Long.toString(issueNote.getNoteID()));
					String query = valueSet.getSelectQuery();
//					System.out.println(query);
					ResultSet result = getControl().executeQuery(query);
					
					try {
						// update the corresponding notes' text
						while (result.next()) {
							valueSet.clear();
							valueSet.addTableName("mantis_bugnote_text_table");						
							valueSet.addInsertValue("note", note.getText());
							valueSet.addEqualCondition("id", result.getString("mantis_bugnote_text_table.id"));
							query = valueSet.getUpdateQuery();
//							System.out.println(query);
							getControl().execute(query);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					break;
				}
			}
		} else{
			insertBugNote(issue.getIssueID(), note.getText());
		}
	}

	public long insertBugNote(long issueID, String note) {
		if (getUserID(getConfig().getAccount()) <= 0)
			return -1;
		try {
			IQueryValueSet valueSet = new MySQLQuerySet();
			valueSet.addTableName("mantis_bugnote_text_table");
			valueSet.addInsertValue("note", note);
			String query = valueSet.getInsertQuery();

			// String updateQuery = "INSERT INTO `mantis_bugnote_text_table`
			// (`id`, `note`) VALUES (NULL, '"
			// + note + "');";
			getControl().execute(query);
//			System.out.println(query);

			valueSet.clear();
			valueSet.addTableName("mantis_bugnote_text_table");
			valueSet.setOrderBy("id", IQueryValueSet.DESC_ORDER);
			query = valueSet.getSelectQuery();

			// 取得剛新增bug note text的id
			// updateQuery = "SELECT `id` FROM `mantis_bugnote_text_table` ORDER
			// BY `id` DESC";
			ResultSet result = getControl().executeQuery(query);
			if (result.next()) {
				int bugNoteID = result.getInt("id");

				valueSet.clear();
				valueSet.addTableName("mantis_bugnote_table");

				valueSet.addInsertValue("bug_id", Long.toString(issueID));
				valueSet.addInsertValue("reporter_id", Integer.toString(this
						.getUserID(getConfig().getAccount())));
				valueSet.addInsertValue("bugnote_text_id", Integer
						.toString(bugNoteID));
				valueSet.addInsertValue("view_state", "10");
				//取得現在的時間
				Timestamp now = new Timestamp(new Date().getTime());
				valueSet.addInsertValue("date_submitted", now.toString());
				valueSet.addInsertValue("last_modified", now.toString());
				valueSet.addInsertValue("note_type", "0");
				valueSet.addInsertValue("note_attr", "");
				valueSet.addInsertValue("time_tracking", "0");

				query = valueSet.getInsertQuery();

				// updateQuery = "INSERT INTO `mantis_bugnote_table` (`id`,
				// `bug_id`, `reporter_id`, `bugnote_text_id`, `view_state`,
				// `date_submitted`, `last_modified`, `note_type`, `note_attr`,
				// `time_tracking`) VALUES (NULL, '"
				// + issueID
				// + "', '"
				// + getUserID(m_prefs.getAccount())
				// + "', '"
				// + bugNoteID + "', '10', NOW(), NOW(), '0', '', '0')";

				getControl().execute(query);
				return bugNoteID;
			} else
				throw new SQLException();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void updateHistoryModifiedDate(IIssue issue, long historyID,
			Date date) {
		Element root = issue.getTagContentRoot();
		@SuppressWarnings("unchecked")
		List<Element> tagElems = root.getChildren();
		boolean modifyFlag = false;
		for (Element tagElem : tagElems) {
			if (Long.parseLong(tagElem.getAttributeValue("id")) == historyID) {
				String tagID = DateUtil.format(date,
						DateUtil._16DIGIT_DATE_TIME_2);
				while (checkDuplicateID(Long.parseLong(tagID), tagElems))
					tagID = Long.toString(Long.parseLong(tagID) + 1);
				tagElem.setAttribute("id", tagID);
				modifyFlag = true;
				break;
			}
		}

		if (modifyFlag) {
			ArrayList<Element> list = new ArrayList<Element>();
			for (Element source : tagElems) {
				int index = 0;
				for (; index < list.size(); index++) {
					Element target = list.get(index);
					if (Long.parseLong(source.getAttributeValue("id")) < Long
							.parseLong(target.getAttributeValue("id")))
						break;
				}
				list.add(index, source);
			}

			// 把child全部清空,加入新排序後的內容
			root.removeContent();
			for (Element tagElem : list)
				root.addContent(tagElem);
			this.updateBugNote(issue);

		}
	}

	private boolean checkDuplicateID(long id, List<Element> list) {
		for (Element element : list) {
			if (Long.parseLong(element.getAttributeValue("id")) == id)
				return true;
		}
		return false;
	}
//刪除retrospective issue
	public void removeNote(String id) {
		IQueryValueSet valueSet = new MySQLQuerySet();
		valueSet.addTableName("mantis_bugnote_table");
		valueSet.addEqualCondition("bug_id", id);
		String query = valueSet.getDeleteQuery();
//		System.out.println(query);
		getControl().execute(query);
		
		valueSet.clear();
		valueSet.addTableName("mantis_bugnote_text_table");
		valueSet.addEqualCondition("id", id);
		query = valueSet.getDeleteQuery();
//		System.out.println(query);
		getControl().execute(query);
		
	}
}
