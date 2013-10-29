package ntut.csie.ezScrum.issue.sql.service.tool.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ntut.csie.ezScrum.issue.sql.service.tool.IValue;
import ntut.csie.ezScrum.issue.sql.service.tool.IValueSet;

public class ValueSet implements IValueSet
{
	String _tablename;
	List<IValue> _list;
	Iterator<IValue> _iterator = null;
	public ValueSet(String tablename)
	{
		_tablename = tablename;
		_list = new ArrayList<IValue>();
	}
	public void add(IValue value)
	{
		_list.add(value);
	}
	public void clear()
	{
		_list.clear();
	}
	
	
	public String getColumns()
	{
		String Columns = new String();
		//�C�X�Ҧ�Value��Name,�]��Name�N�����
		Iterator<IValue> iter = _list.iterator();
		
		//�P�_�O�_�������n��o
		if(_list.size() == 0)
		{
			return Columns;
		}
		else
		{
			Columns = iter.next().getName();
		}
		
		//�p�G�٦���L���n���[�W�h , �N�����b���e�[�� ","
		while(iter.hasNext())
		{
			Columns += ","+iter.next().getName();
		}
		
		return Columns;
	}
	public String getTableName()
	{
		return _tablename;
	}
	public boolean hasNext()
	{
		return _iterator.hasNext();
	}
	public void begin()
	{
		_iterator = _list.iterator();
	}
	
	public IValue getValue()
	{
		if(_iterator.hasNext())
		{
			return _iterator.next();
		}
		
		return null;
	}
	
}
