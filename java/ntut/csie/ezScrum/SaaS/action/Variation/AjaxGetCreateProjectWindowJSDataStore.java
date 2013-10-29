package ntut.csie.ezScrum.SaaS.action.Variation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetCreateProjectWindowJSDataStore extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		String result;
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("Ext.ns('ezScrum');"+ "\n"+
"Ext.ns('ezScrum.layout');"+ "\n"+
"Ext.ns('ezScrum.window');"+ "\n"+
""+ "\n"+
"/* SaveProjectResult */"+ "\n"+
"var CreateProjectResult = Ext.data.Record.create( [ 'Result' , 'IP', 'DBName', 'ID']);"+ "\n"+
""+ "\n"+
"var createResultReader = new Ext.data.XmlReader( {"+ "\n"+
"	record : 'CreateProjectResult'"+ "\n"+
"}, CreateProjectResult);"+ "\n"+
""+ "\n"+
"var createResultStore = new Ext.data.Store( {"+ "\n"+
"	fields : [ {"+ "\n"+
"		name : 'Result'"+ "\n"+
"	} ,"+ "\n"+
"	{"+ "\n"+
"		name:'IP'"+ "\n"+
"	},"+ "\n"+
"	{"+ "\n"+
"		name:'DBName'"+ "\n"+
"	},"+ "\n"+
"	{"+ "\n"+
"		name:'ID'"+ "\n"+
"	}],"+ "\n"+
"	reader : createResultReader"+ "\n"+
"});"+ "\n"+
"\n"+
"/*-----------------------------------------------------------"+ "\n"+
" *	顯示DataBase選單  "+ "\n"+
" -------------------------------------------------------------*/"+ "\n"+
"//var sqlList = [ [ 'Default(Local Database)' ], [ 'MySQL' ] ]; // Local DB(H2) 未開放，未完整驗證功能性"+ "\n"+
"/*"+ "\n"+
" var sqlList = [['MySQL']];"+ "\n"+
"var sqlDataType = new Ext.data.ArrayStore( {"+ "\n"+
"	// store configs"+ "\n"+
"	autoDestory : true,"+ "\n"+
"	storeId : 'sqlDataType',"+ "\n"+
"	// reader configs"+ "\n"+
"	idIndex : 0,"+ "\n"+
"	fields : [ 'SQLName' ],"+ "\n"+
"	data : sqlList"+ "\n"+
"});"+ "\n"+
"\n"+
"var sqlCombo = new Ext.form.ComboBox( {"+ "\n"+
"	store : sqlDataType,"+ "\n"+
"	displayField : 'SQLName',"+ "\n"+
"	fieldLabel : 'SQL Type',"+ "\n"+
"	typeAhead : true,"+ "\n"+
"	mode : 'local',"+ "\n"+
"	triggerAction : 'all',"+ "\n"+
"	emptyText : 'Select a DataBase',"+ "\n"+
"	selectOnFocus : true,"+ "\n"+
"	allowBlank : false,"+ "\n"+
"	name : 'SQLType'"+ "\n"+
"});"+ "\n"+
"*/"+ "\n"+
"\n"+
"ezScrum.CreateProjectForm = Ext.extend(Ext.form.FormPanel, {"+ "\n"+
"	bodyStyle : 'padding:15px',"+ "\n"+
"	border : false,"+ "\n"+
"	defaultType : 'textfield',"+ "\n"+
"	labelAlign : 'right',"+ "\n"+
"	labelWidth : 150,"+ "\n"+
"	defaults : {"+ "\n"+
"		width : 450,"+ "\n"+
"		msgTarget : 'side'"+ "\n"+
"	},"+ "\n"+
"	monitorValid : true,"+ "\n"+
"	initComponent : function() {"+ "\n"+
"		var config = {"+ "\n"+
"			url : 'AjaxCreateProject.do',"+ "\n"+
"			items : [ {"+ "\n"+
"				name : 'from',"+ "\n"+
"				originalValue : 'createProject',"+ "\n"+
"				hidden : true"+ "\n"+
"			}, {"+ "\n"+
"				fieldLabel : 'Project Name',"+ "\n"+
"				name : 'Name',"+ "\n"+
"				allowBlank : false,"+ "\n"+
"				maxLength : 128,"+ "\n"+
"				regex : /^[\\w-_()~ ]*$/, // support a-z,A-Z,0-9,-,_,(,),~還有空格"+ "\n"+
"				regexText : 'Only a-z,A-Z,0-9,-,_,(,),~,space allowed.'"+ "\n"+
"			}, {"+ "\n"+
"				fieldLabel : 'Project Display Name',"+ "\n"+
"				name : 'DisplayName',"+ "\n"+
"				allowBlank : false,"+ "\n"+
"				maxLength : 128"+ "\n"+
"			}, {"+ "\n"+
"				fieldLabel : 'Comment',"+ "\n"+
"				xtype : 'textarea',"+ "\n"+
"				name : 'Comment',"+ "\n"+
"				height : 50"+ "\n"+
"			}, {"+ "\n"+
"				fieldLabel : 'Project Manager',"+ "\n"+
"				name : 'ProjectManager'"+ "\n"+
"			}, {"+ "\n"+
"				fieldLabel : 'Attach File Max Size (Default: 2MB)',"+ "\n"+
"				name : 'AttachFileSize',"+ "\n"+
"				vtype : 'Number'"+ "\n"+
"			}],"+ "\n"+
"			buttons : [{"+ "\n"+
"				formBind : true,"+ "\n"+
"				text : 'Submit',"+ "\n"+
"				scope : this,"+ "\n"+
"				handler : this.submit,"+ "\n"+
"				disabled : true"+ "\n"+
"			}, {"+ "\n"+
"				text : 'Cancel',"+ "\n"+
"				scope : this,"+ "\n"+
"				handler : function() {"+ "\n"+
"					this.ownerCt.hide();"+ "\n"+
"				}"+ "\n"+
"			} ]"+ "\n"+
"		}"+ "\n"+
"		"+ "\n"+
"		"+ "\n"+
"		Ext.apply(this, Ext.apply(this.initialConfig, config));"+ "\n"+
"		ezScrum.CreateProjectForm.superclass.initComponent.apply(this,"+ "\n"+
"				arguments);"+ "\n"+
"		"+ "\n"+
"		this.addEvents('CreateSuccess', 'CreateFailure', 'InitDatabase', 'CreateDatabase');"+ "\n"+
"	},"+ "\n"+
"	onRender : function() {"+ "\n"+
"		ezScrum.CreateProjectForm.superclass.onRender.apply(this, arguments);"+ "\n"+
"		this.getForm().waitMsgTarget = this.getEl();"+ "\n"+
"	},"+ "\n"+
"	submit : function() {"+ "\n"+
"		var form = this.getForm();"+ "\n"+
"		var obj = this;"+ "\n"+
"		Ext.Ajax.request( {"+ "\n"+
"			url : 'AjaxCreateProject.do',"+ "\n"+
"			success : function(response) {"+ "\n"+
"				obj.onSuccess(response);"+ "\n"+
"			},"+ "\n"+
"			failure : function(response) {"+ "\n"+
"				obj.onFailure(response);"+ "\n"+
"			},"+ "\n"+
"			params : form.getValues()"+ "\n"+
"		});"+ "\n"+
"	},"+ "\n"+
"	onSuccess : function(response) {"+ "\n"+
"		var myMask = new Ext.LoadMask(this.getEl(), {"+ "\n"+
"			msg : 'Please wait...'"+ "\n"+
"		});"+ "\n"+
"		"+ "\n"+
"		createResultStore.loadData(response.responseXML);"+ "\n"+
"		"+ "\n"+
"		if (createResultStore.getAt(0).data['Result'] == 'Success')"+ "\n"+
"			this.fireEvent('CreateSuccess', this, createResultStore.getAt(0).data['ID']);"+ "\n"+
"		else if (createResultStore.getAt(0).data['Result'] == 'Failure')"+ "\n"+
"			this.fireEvent('CreateFailure', this, response);"+ "\n"+
"		else if (createResultStore.getAt(0).data['Result'] == 'InitDatabase') {"+ "\n"+
"			response.ip = createResultStore.getAt(0).data['IP'];"+ "\n"+
"			response.dbName = createResultStore.getAt(0).data['DBName'];"+ "\n"+
"			this.fireEvent('InitDatabase', this.getForm(), response);"+ "\n"+
"		}"+ "\n"+
"        else if(createResultStore.getAt(0).data['Result'] == 'CreateDatabase') {"+ "\n"+
"            response.ip = createResultStore.getAt(0).data['IP'];"+ "\n"+
"            response.dbName = createResultStore.getAt(0).data['DBName'];"+ "\n"+
"            this.fireEvent('CreateDatabase', this.getForm(), response);"+ "\n"+
"        }"+ "\n"+
"        else if(createResultStore.getAt(0).data['Result'] == 'Connect_Error') {"+ "\n"+
"        	this.fireEvent('CreateFailure', this, response);"+ "\n"+
"        }"+ "\n"+
"		myMask.hide();"+ "\n"+
"		"+ "\n"+
"	},"+ "\n"+
"	onFailure : function(response) {"+ "\n"+
"		var myMask = new Ext.LoadMask(this.getEl(), {"+ "\n"+
"			msg : 'Please wait...'"+ "\n"+
"		});"+ "\n"+
"		myMask.hide();"+ "\n"+
"		this.fireEvent('CreateFailure', this, response);"+ "\n"+
"	},"+ "\n"+
"	reset : function() {"+ "\n"+
"		this.getForm().reset();"+ "\n"+
"	}"+ "\n"+
"});"+ "\n"+
"\n"+
"Ext.reg('CreateProjectForm', ezScrum.CreateProjectForm);"+ "\n"+
"\n"+
"ezScrum.window.CreateProjectWindow = Ext.extend(ezScrum.layout.Window, {"+ "\n"+
"	id : 'CreateProjectWindow',"+ "\n"+
"	title : 'Create New Project',"+ "\n"+
"	width : 700,"+ "\n"+
"	modal : true,"+ "\n"+
"	constrain : true,"+ "\n"+
"	closeAction : 'hide',"+ "\n"+
"	initComponent : function() {"+ "\n"+
"		var config = {"+ "\n"+
"			layout : 'form',"+ "\n"+
"			items : [ {"+ "\n"+
"				xtype : 'CreateProjectForm'"+ "\n"+
"			} ]"+ "\n"+
"		}"+ "\n"+
"		Ext.apply(this, Ext.apply(this.initialConfig, config));"+ "\n"+
"		ezScrum.window.CreateProjectWindow.superclass.initComponent.apply(this, arguments);"+ "\n"+
"		"+ "\n"+
"		this.addEvents('CreateSuccess', 'CreateFailure', 'InitDatabase');"+ "\n"+
"		"+ "\n"+
"		this.items.get(0).on('CreateSuccess', function(obj, response) {"+ "\n"+
"			this.fireEvent('CreateSuccess', this, obj, response);"+ "\n"+
"		}, this);"+ "\n"+
"		this.items.get(0).on('CreateFailure', function(obj, response) {"+ "\n"+
"			this.fireEvent('CreateFailure', this, obj, response);"+ "\n"+
"		}, this);"+ "\n"+
"		this.items.get(0).on('InitDatabase', function(obj, response) {"+ "\n"+
"			this.fireEvent('InitDatabase', this, obj, response);"+ "\n"+
"		}, this),"+ "\n"+
"        this.items.get(0).on('CreateDatabase',function(obj,response) {"+ "\n"+
"            this.fireEvent('CreateDatabase', this, obj,response) "+ "\n"+
"        },this);"+ "\n"+
"	}, "+ "\n"+
"	showWidget : function(sprint) {"+ "\n"+
"		this.items.get(0).reset();"+ "\n"+
"		this.show();"+ "\n"+
"	}"+ "\n"+
"});");
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