package ntut.csie.ezScrum.issue.sql.service.tool;

public interface IValueSet
{
	public String getColumns();
	public String getTableName();
	public IValue getValue();
	public void clear();
	public void begin();
	public boolean hasNext();
	public void add(IValue value);
}
