package ntut.csie.ezScrum.issue.internal;

import java.util.ArrayList;
import java.util.List;

// 儲存客製化欄位資料
public class IssueTypeField {
	public enum Category {
	    Combo, Textbox, Textarea, Number, Date
	}
	
	private int _fieldID;
	private String _fieldName;
	private Category _category;
	private String _fieldValue;
	// 儲存 Combo 選項 
	private List<String> _comboOptions;
	
	public IssueTypeField(){
		this._fieldID = 0;
		this._fieldName = this._fieldValue = "";
		this._category = Category.Textbox;
		this._comboOptions = null;
	}
	
	public IssueTypeField(int id, String name, String category, String value){
		this._fieldID = id;
		this._fieldName = name;
		this._fieldValue = value;
		this._comboOptions = null;
		
		decideCategory(category);
	}
	
	private void decideCategory(String category){
		if (category.equals("Combo")){
			this._category = Category.Combo;
			this._comboOptions = new ArrayList<String>();
		}
		else if (category.equals("Textbox"))
			this._category = Category.Textbox;
		else if (category.equals("Textarea"))
			this._category = Category.Textarea;
		else if (category.equals("Number"))
			this._category = Category.Number;
		else if (category.equals("Textbox"))
			this._category = Category.Date;
	}
	
	public void setFieldID(int id){
		this._fieldID = id;
	}
	
	public void setFieldName(String name){
		this._fieldName = name;
	}
	
	public void setFieldCategory(String category){
		decideCategory(category);
	}
	
	public void setFieldValue(String value){
		this._fieldValue = value;
	}
	
	public void setFieldOption(String name){
		this._comboOptions.add(name);
	}
	
	public int getFieldID(){
		return this._fieldID;
	}
	
	public String getFieldName(){
		return this._fieldName;
	}
	
	public Category getFieldCategory(){
		return this._category;
	}
	
	public String getFieldValue(){
		return this._fieldValue;
	}
	
	public List<String> getFieldOptions(){
		return this._comboOptions;
	}
}
