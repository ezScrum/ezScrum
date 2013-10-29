package ntut.csie.ezScrum.SaaS.action.Variation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetProjectDescFormPanelJSDataStore extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		String result;
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("// the form is for Summary Page" + "\n"+
"ProjectDescForm = Ext.extend(ezScrum.layout.InfoForm, {" + "\n"+
"	id			: 'ProjectDesc'," + "\n"+
"	title		: 'Project Description'," + "\n"+
"    store		: ProjectDescStore," + "\n"+
"	initComponent : function() {" + "\n"+
"		var config = {" + "\n"+
"			url		: 'GetProjectDescription.do'," + "\n"+
"			items	: [ ProjectDescItem ]" + "\n"+
"		}" + "\n"+
"		" + "\n"+
"		Ext.apply(this, Ext.apply(this.initialConfig, config));" + "\n"+
"		ProjectDescForm.superclass.initComponent.apply(this, arguments);" + "\n"+
"	}," + "\n"+
"	loadDataModel: function() {" + "\n"+
"		var obj = this;" + "\n"+
"		Ext.Ajax.request({" + "\n"+
"			url : obj.url," + "\n"+
"			success: function(response) {" + "\n"+
"	    		ProjectDescStore.loadData(Ext.decode(response.responseText));" + "\n"+
"	    		var record = ProjectDescStore.getAt(0);" + "\n"+
"    			obj.setDataModel(record);" + "\n"+
"			}," + "\n"+
"			failure: function(response) {" + "\n"+
"				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');" + "\n"+
"			}" + "\n"+
"		});" + "\n"+
"	}," + "\n"+
"	setDataModel: function(record) {" + "\n"+
"    	var replaced_comment = replaceJsonSpecialChar(record.get('Commnet'));" + "\n"+
"    	var replaced_projectManager = replaceJsonSpecialChar(record.get('ProjectManager'));" + "\n"+
"    	" + "\n"+
"		this.getForm().setValues({" + "\n"+
"			Commnet: replaced_comment," + "\n"+
"			ProjectManager: replaced_projectManager," + "\n"+ 
"			ProjectCreateDate: record.get('ProjectCreateDate')" + "\n"+
"		});" + "\n"+
"	}" + "\n"+
"});" + "\n"+
"Ext.reg('ProjectDescForm', ProjectDescForm);" + "\n"+
"\n"+
"// the form is for Modify Config Page" + "\n"+
"ProjectModifyForm = Ext.extend(ezScrum.layout.InfoForm, {" + "\n"+
"	id			: 'ProjectDescModify'," + "\n"+
"	title		: 'Project Preference'," + "\n"+
"	buttonAlign	: 'left'," + "\n"+
"	store		: ProjectModifyStore," + "\n"+
"	initComponent : function() {" + "\n"+
"		var config = {" + "\n"+
"			url			: 'GetProjectDescription.do'," + "\n"+
"			modify_url	: 'ModifyProjectDescription.do',	" + "\n"+
"			items 		: [ ProjectDescModifyItem ]," + "\n"+
"	        buttons : [{" + "\n"+
"	        	text     : 'Modify It'," + "\n"+
"	        	scope    : this," + "\n"+
"	        	handler  : this.doModify," + "\n"+
"	        	disabled : false" + "\n"+
"	        }]" + "\n"+
"		}" + "\n"+
"		" + "\n"+
"		Ext.apply(this, Ext.apply(this.initialConfig, config));" + "\n"+
"		ProjectModifyForm.superclass.initComponent.apply(this, arguments);" + "\n"+
"	}," + "\n"+
"    loadDataModel: function() {" + "\n"+
"    	var obj = this;" + "\n"+
"    	MainLoadMaskShow();" + "\n"+
"    	Ext.Ajax.request({" + "\n"+
"    		url : obj.url," + "\n"+
"    		success: function(response) {" + "\n"+
"    			ConfirmWidget.loadData(response);" + "\n"+
"    			if (ConfirmWidget.confirmAction()) {" + "\n"+
"    				ProjectDescStore.loadData(Ext.decode(response.responseText));" + "\n"+
"    				var record = ProjectDescStore.getAt(0);" + "\n"+
"					obj.setDataModel(record);" + "\n"+
"					" + "\n"+
"					MainLoadMaskHide();" + "\n"+
"    			}" + "\n"+
"    		}," + "\n"+
"    		failure: function(response) {" + "\n"+
"    			MainLoadMaskHide();" + "\n"+
"    			" + "\n"+
"    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');" + "\n"+
"    		}" + "\n"+
"    	});" + "\n"+
"    }," + "\n"+
"    setDataModel: function(record) {" + "\n"+
"    	var replaced_projectName = replaceJsonSpecialChar(record.get('ProjectName'));" + "\n"+
"    	var replaced_displayprojectName = replaceJsonSpecialChar(record.get('ProjectDisplayName'));" + "\n"+
"    	var replaced_comment = replaceJsonSpecialChar(record.get('Commnet'));" + "\n"+
"    	var replaced_projectManager = replaceJsonSpecialChar(record.get('ProjectManager'));" + "\n"+
"    	" + "\n"+
"    	this.getForm().setValues({" + "\n"+
"    		ProjectName: replaced_projectName," + "\n"+
"    		ProjectDisplayName: replaced_displayprojectName," + "\n"+
"    		Commnet: replaced_comment," + "\n"+
"    		ProjectManager: replaced_projectManager," + "\n"+ 
"    		AttachFileSize: record.get('AttachFileSize')" + "\n"+
"    	});" + "\n"+
"    }," + "\n"+
"    doModify: function() {" + "\n"+
"		var obj = this;" + "\n"+
"    	var form = this.getForm();" + "\n"+
"    	var loadmask = new Ext.LoadMask(this.getEl(), {msg:'loading info...'});" + "\n"+
"		loadmask.show();" + "\n"+
"    	Ext.Ajax.request({" + "\n"+
"    		url: obj.modify_url," + "\n"+
"    		params: form.getValues()," + "\n"+
"    		success: function(response) {" + "\n"+
"    			ConfirmWidget.loadData(response);" + "\n"+
"    			if (ConfirmWidget.confirmAction()) {" + "\n"+
"    				var result = response.responseText;" + "\n"+
"    				if (result == 'success') {" + "\n"+
"    					Ext.example.msg('Modify Project', 'Success.');" + "\n"+
"    				} else {" + "\n"+
"    					Ext.example.msg('Modify Project', 'Sorry, the action is failure.');" + "\n"+
"    				}" + "\n"+
"    				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:'loading info...'});" + "\n"+
"    				loadmask.hide();" + "\n"+
"    			}" + "\n"+
"    		}," + "\n"+
"    		failure:function(response){" + "\n"+
"    			var loadmask = new Ext.LoadMask(obj.getEl(), {msg:'loading info...'});" + "\n"+
"				loadmask.hide();" + "\n"+
"    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');" + "\n"+
"    		}" + "\n"+
"    	});" + "\n"+
"	}" + "\n"+
"});" + "\n"+
"Ext.reg('ProjectModifyForm', ProjectModifyForm);");
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