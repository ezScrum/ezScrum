package ntut.csie.ezScrum.issue.sql.service.internal;

import ntut.csie.ezScrum.issue.sql.service.tool.IValue;

public class QueryValue implements IValue {

	String m_name;
	String m_value;

	public QueryValue(String name, String value) {
		m_name = name;
		m_value = value;
	}

	@Override
	public String getName() {
		return "`" + m_name + "`";
	}

	@Override
	public String getValue() {
		if (isSQLKeyWord())
			return m_value;
		else
			return "'" + m_value + "'";
	}

	private boolean isSQLKeyWord() {
		if (m_value == null)
			return false;
		if (m_value.equals("NOW()"))
			return true;
		return false;
	}

}
