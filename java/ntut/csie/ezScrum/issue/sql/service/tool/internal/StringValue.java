package ntut.csie.ezScrum.issue.sql.service.tool.internal;

import ntut.csie.ezScrum.issue.sql.service.tool.IValue;

public class StringValue implements IValue
{
	String _name;
	String _value;
	public StringValue(String name,String value)
	{
		_name = name;
		_value = value;
	}
	public String getName()
	{
		return _name;
	}
	public String getValue()
	{
		return "'"+_value+"'";
	}
	public String toString()
	{
		return null;
	}
}
