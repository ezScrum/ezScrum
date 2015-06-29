package ntut.csie.ezScrum.issue.sql.service.core;

import ntut.csie.ezScrum.issue.sql.service.tool.IValue;

//延伸舊有的介面,來擴充先前功能需求的不足
public interface IQueryValueSet {
	final public static String ASC_ORDER = "ASC";
	final public static String DESC_ORDER = "DESC";	
	
	public void addResultRow(String rowName);
	public void addInsertValue(IValue value);
	
	public void addTableName(String tableName);
	public void joinTableName(String joinName,String on); //用join方式串連Table
	
	public void addInsertValue(String name, String value);
	public void addInsertValue(String name, long value);
	
	// Left Join by Field
	public void addLeftJoin(String tableName, String name1, String name2);
	public void addNotNullCondition(String name);
	public void addEqualCondition(String name, String value);//欄位與數值
	public void addEqualCondition(String name, long value);//欄位與數值
	public void addLessCondition(String name, String value);//欄位與數值
	public void addBigCondition(String name, String value);//欄位與數值
	public void addFieldEqualCondition(String name1, String name2);//欄位與欄位
	public void addLikeCondition(String name, String value);//欄位與字串
	public void addTextFieldEqualCondition(String name, String value);
	
	public void addLikeConditionXtable(String name, String value);//欄位與字串

	public String getColumns();

	public String getColumnValues();

	public String getConditions();

	public String getTableNames();
	
	public String getLeftJoins();
	
	public String getColumnsAndValues();
	
	public String getSelectQuery();
	
	public String getInsertQuery();
	
	public String getUpdateQuery();
	
	public String getDeleteQuery();
	//Kanban 在使用
	public String getMaxQuery(String tableName, String column);
	
	public void setOrderBy(String name, String desc);
	
	public void clear();
}
