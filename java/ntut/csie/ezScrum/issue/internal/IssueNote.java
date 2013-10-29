package ntut.csie.ezScrum.issue.internal;

import java.util.Date;

import ntut.csie.ezScrum.issue.core.IIssueNote;

public class IssueNote implements IIssueNote {

	private String m_handler;
	private long m_issueID = 0;
	private long m_noteID = 0;
	private String m_text;
	private long m_modifiedDate;
	private long m_submittedDate;
	public Date getSubmittedDateDate()
	{return new Date(m_submittedDate);}
	@Override
	public String getHandler() {
		return m_handler;
	}

	@Override
	public long getIssueID() {
		return m_issueID;
	}

	@Override
	public long getNoteID() {
		return m_noteID;
	}

	@Override
	public String getText() {
		return m_text;
	}

	@Override
	public void setHandler(String handler) {
		m_handler = handler;
	}

	@Override
	public void setIssueID(long id) {
		m_issueID = id;
	}

	@Override
	public void setNoteID(long id) {
		m_noteID = id;
	}

	@Override
	public void setText(String text) {
		m_text = text;
	}

	@Override
	public long getModifiedDate() {
		return m_modifiedDate;
	}

	@Override
	public long getSubmittedDate() {
		return m_submittedDate;
	}

	@Override
	public void setModifiedDate(long date) {
		m_modifiedDate = date;
	}

	@Override
	public void setSubmittedDate(long date) {
		m_submittedDate = date;
	}

}
