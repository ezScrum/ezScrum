package ntut.csie.ezScrum.SaaS.action.Variation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetProjectDescriptionJSDataStore extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		//取得專案名稱
		
		String result;
		try{
			StringBuilder sb = new StringBuilder();
			sb.append(
					"var ProjectRecord = Ext.data.Record.create([" + "\n"+
					"'ProjectName', 'ProjectDisplayName', 'AttachFileSize',	'Commnet', " + "\n"+
					"'ProjectManager', 'ProjectCreateDate' ]);"+ "\n"+

					"var ProjectReader = new Ext.data.JsonReader({"+ "\n"+
					"	id: 'ID'"+ "\n"+
					"}, ProjectRecord);"+ "\n"+

					"var ProjectModifyStore = new Ext.data.Store({"+ "\n"+
					"	fields : ["+ "\n"+
					"		{name : 'ProjectName'},"+ "\n"+ 
					"		{name : 'ProjectDisplayName'},"+ "\n"+
					"		{name : 'AttachFileSize'},"+ "\n"+
					"		{name : 'Commnet'},"+ "\n"+
					"		{name : 'ProjectManager'}"+ "\n"+
					"	],"+ "\n"+
					"	reader : ProjectReader"+ "\n"+
					"});"+ "\n"+
					
					"\n"+
					
					"var ProjectDescStore = new Ext.data.Store({"+ "\n"+
					"	fields : ["+ "\n"+
					"		{name : 'Commnet'},"+ "\n"+
					"		{name : 'ProjectManager'},"+ "\n"+
					"		{name : 'ProjectCreateDate'}"+ "\n"+
					"	],"+ "\n"+
					"	reader : ProjectReader"+ "\n"+
					"});"+ "\n"+
					
					 "\n"+
					
					"var ProjectDescItem = ["+ "\n"+
					"	{fieldLabel: 'Comment', name: 'Commnet', xtype:'textfield', anchor: '50%', readOnly: true},"+ "\n"+
					"	{fieldLabel: 'Project Manager', name: 'ProjectManager', xtype:'textfield', anchor: '50%', readOnly: true},"+ "\n"+
					"	{fieldLabel: 'Project CreateDate', name: 'ProjectCreateDate', xtype:'textfield', anchor: '50%', readOnly: true}"+ "\n"+
					"];"+ "\n"+

					 "\n"+
					
					"var ProjectDescModifyItem = ["+ "\n"+
					"	{fieldLabel: 'Project Name', name: 'ProjectName', xtype:'textfield', readOnly: true, anchor: '50%'},"+ "\n"+
					"	{fieldLabel: 'Project Dislpay Name', name: 'ProjectDisplayName', xtype:'textfield', anchor: '50%'},"+ "\n"+
					"  	{fieldLabel: 'Comment', name: 'Commnet', xtype:'textarea', anchor: '50%', height: 50},"+ "\n"+
					"  	{fieldLabel: 'Project Manager', name: 'ProjectManager', xtype:'textfield', anchor: '50%'},"+ "\n"+
					"  	{fieldLabel: 'Attach File Max Size (Default: 2MB)', name: 'AttachFileSize', xtype:'numberfield', anchor: '50%'}"+ "\n"+
					"];"
					);
			result = sb.toString();
		} catch(Exception e){
			result = "<Permission><Result>false</Result></Permission>";
		}
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}