package ntut.csie.ezScrum.SaaS.action.Variation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetViewListJSPDataStore extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		String result;
		try{
			StringBuilder sb = new StringBuilder();
			sb.append("Ext.ns('ezScrum.window');" + "\n"+
			"\n"+
			"var Project = Ext.data.Record.create(['ID', 'Name', 'Comment', 'ProjectManager', 'CreateDate', 'DemoDate']);" + "\n"+
			"\n"+
			"var ProjectReader = new Ext.data.XmlReader({" + "\n"+
			"	   record: 'Project'," + "\n"+
			"	   idPath : 'ID'," + "\n"+
			"	   successProperty: 'Result'" + "\n"+
			"}, Project);" + "\n"+
			"\n"+
			"var ProjectStore = new Ext.data.Store({" + "\n"+
			"	fields:[" + "\n"+
			"		{name : 'ID'}," + "\n"+
			"		{name : 'Name'}," + "\n"+
			"		{name : 'Comment'}," + "\n"+
			"		{name : 'ProjectManager'}," + "\n"+
			"		{name : 'CreateDate'}," + "\n"+
			"		{name : 'DemoDate'}" + "\n"+
			"	]," + "\n"+
			"	reader : ProjectReader" + "\n"+
			"});" + "\n"+
			"\n"+
			"var CheckCreateProject = Ext.data.Record.create(['Check']);" + "\n"+
			"\n"+
			"var CheckReader = new Ext.data.XmlReader({" + "\n"+
			"	record: 'CheckCreateProject'" + "\n"+
			"}, CheckCreateProject);" + "\n"+
			"\n"+
			"var CheckStore = new Ext.data.Store({" + "\n"+
			"	fields:[" + "\n"+
			"		{name : 'Check'}" + "\n"+
			"	]," + "\n"+
			"	reader: CheckReader" + "\n"+
			"});" + "\n"+
			"\n"+
			"var CreateProjectWindow = new ezScrum.window.CreateProjectWindow({" + "\n"+
			"	listeners: {" + "\n"+
			"		CreateSuccess:function(win, form, projectID){" + "\n"+			
			"	 		this.hide();" + "\n"+
			"	 		alert('1321312321321Create success. Redirect to view project summary.');" + "\n"+
			"	 		document.location.href = './viewProject.do?PID=' + projectID;" + "\n"+
			"		}," + "\n"+
			"		CreateFailure:function(win, form, response){" + "\n"+
			"			// Create Project Error" + "\n"+
			"			alert('Create project on ITS failed. Please check the information of ITS and Project');" + "\n"+
			"		}," + "\n"+
			"		// 資料庫需要進行初始化的動作" + "\n"+
			"		InitDatabase:function(win,form,response)" + "\n"+
			"		{" + "\n"+
			"			// Show a dialog using config options:" + "\n"+
			"			Ext.Msg.show({" + "\n"+
			"			   title:'The Database need be initialized'," + "\n"+
			"			   msg: 'The Database <b>'+response.dbName+'</b> at <b>'+response.ip+'</b> will be initiated , and all data of the database will be clean'," + "\n"+
			"			   buttons: Ext.Msg.YESNO," + "\n"+
			"			   fn: function(btn){" + "\n"+
			"					// 如果同意那就呼叫InitialDB的Action" + "\n"+
			"					if(btn == 'yes')" + "\n"+
			"					{" + "\n"+
			"						Ext.Ajax.request( {" + "\n"+
			"							url : 'initialDatabase.do'," + "\n"+
			"							success : function(response) {" + "\n"+
			"								Ext.MessageBox.alert('Initial DataBase','Initial DataBase Success, press Submit to continue.');" + "\n"+
			"							}," + "\n"+
			"							failure : function(response) {" + "\n"+
			"								Ext.MessageBox.alert('Initial DataBase','Initial DataBase Failure.');" + "\n"+
			"							}," + "\n"+
			"							params : form.getValues()" + "\n"+
			"						});" + "\n"+
			"					}" + "\n"+
			"			}," + "\n"+
			"			   icon: Ext.MessageBox.WARNING" + "\n"+
			"			});" + "\n"+	
			"		} ," + "\n"+
			"        CreateDatabase:function(win,form,response)" + "\n"+
			"        {" + "\n"+
			"            // Show a dialog using config options:" + "\n"+
			"            Ext.Msg.show({" + "\n"+
			"               title:'The Database need be created'," + "\n"+
			"               msg: 'The Database <b>'+response.dbName+'</b> at <b>'+response.ip+'</b> will be created.'," + "\n"+
			"               buttons: Ext.Msg.YESNO," + "\n"+
			"               fn: function(btn){" + "\n"+
			"                    // 如果同意那就呼叫InitialDB的Action" + "\n"+
			"                    if(btn == 'yes')" + "\n"+
			"                    {" + "\n"+
			"                        Ext.Ajax.request( {" + "\n"+
			"                            url : 'createDatabase.do'," + "\n"+
			"                            success : function(response) {" + "\n"+
			"                                Ext.MessageBox.alert('Success!','The Database has be created，please click the submit to next step ');" + "\n"+
			"                            }," + "\n"+
			"                            failure : function(response) {" + "\n"+
			"                                alert('Create Database Falilure');" + "\n"+
			"                            }," + "\n"+
			"                            params : form.getValues()" + "\n"+
			"                        });" + "\n"+
			"                    }" + "\n"+
			"               }," + "\n"+
			"               icon: Ext.MessageBox.WARNING" + "\n"+
			"            }); " + "\n"+
			"        }" + "\n"+
			"	}" + "\n"+
			"});" + "\n"+
			"\n"+
			"var ProjectsGird = new Ext.grid.GridPanel({" + "\n"+
			"	id 			: 'Projects_GirdPanel'," + "\n"+
			"	title		: 'Project List'," + "\n"+
			"	region		: 'center'," + "\n"+
			"    collapsible	: false," + "\n"+
			"	border		: false," + "\n"+
			"	frame		: false," + "\n"+
			"    stripeRows	: true," + "\n"+
			"	store		: ProjectStore," + "\n"+
			"    tbar		: []," + "\n"+
			"	viewConfig	: {" + "\n"+
			"        forceFit: true" + "\n"+
			"    }," + "\n"+
			"	colModel: new Ext.grid.ColumnModel({" + "\n"+
			"		columns: [" + "\n"+
			"			{dataIndex: 'Name',header: 'Name', width: 100}," + "\n"+
			"            {dataIndex: 'Comment',header: 'Comment', width: 150}," + "\n"+
			"            {dataIndex: 'ProjectManager',header: 'Project Manager', width: 100}," + "\n"+
			"            {dataIndex: 'CreateDate',header: 'Create Date', width: 100}," + "\n"+
			"            {dataIndex: 'DemoDate',header: 'Demo Date', width: 100}" + "\n"+
			"		]" + "\n"+
			"	})," + "\n"+
			"	sm: new Ext.grid.RowSelectionModel({" + "\n"+
			"    	singleSelect:true" + "\n"+
			"    })" + "\n"+
			"});" + "\n"+
			"\n"+
			"ProjectsGird.getSelectionModel().on({'selectionchange':{buffer:10, fn:function(){" + "\n"+
			"	var single = this.getCount() == 1;" + "\n"+
			"	if(single) {" + "\n"+
			"		var record = this.getSelected();" + "\n"+
			"		replaceURL( './viewProject.do?PID=' + record.data['ID'] );" + "\n"+
			"		//document.location.href = './viewProject.do?PID=' + record.data['ID'];" + "\n"+
			"	}" + "\n"+
			"}}});" + "\n"+
			"\n"+
			"ezScrumProjectList = Ext.extend(Ext.Viewport, {" + "\n"+
			"	id: 'ProjectListContentLayout'," + "\n"+
			"	layout: 'border'," + "\n"+
			"	initComponent: function() {" + "\n"+
			"        this.items = [" + "\n"+
			"			ezScrum.Project_TopPanel," + "\n"+
			"			ProjectsGird," + "\n"+
			"			ezScrum.FooterPanel" + "\n"+
			"        ];" + "\n"+
			"        " + "\n"+
			"        ezScrumProjectList.superclass.initComponent.call(this);" + "\n"+
			"    }," + "\n"+
			"	listeners:{" + "\n"+
			"		beforerender:function() {" + "\n"+
			"			if (CreateProjectPermission) {" + "\n"+
			"				var tbar = ProjectsGird.getTopToolbar();" + "\n"+
			"				var btn = tbar.add({" + "\n"+
			"					id:'createProjectBtn', " + "\n"+
			"					text:'Create Project', " + "\n"+
			"					icon:'images/add3.png'," + "\n"+
			"					handler: function() {" + "\n"+
			"						CreateProjectWindow.showWidget();" + "\n"+
			"					}" + "\n"+
			"				});" + "\n"+
			"			}" + "\n"+
			"		}," + "\n"+
			"		render: function() {" + "\n"+
			"			this.loadDataModel();" + "\n"+
			"		}" + "\n"+
			"	}," + "\n"+
			"	loadDataModel: function() {" + "\n"+
			"		var obj = this;" + "\n"+
			"		var loadmask = new Ext.LoadMask(this.getEl(), {msg:'loading info...'});" + "\n"+
			"		loadmask.show();" + "\n"+
			"		Ext.Ajax.request({" + "\n"+
			"			url:'viewProjectList.do'," + "\n"+
			"			success : function(response) {" + "\n"+
			"				ProjectStore.loadData(response.responseXML);" + "\n"+
			"				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:'loading info...'});" + "\n"+
			"				loadmask.hide();" + "\n"+
			"			}," + "\n"+
			"			failure : function(){" + "\n"+
			"				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:'loading info...'});" + "\n"+
			"    			loadmask.hide();" + "\n"+
			"				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');" + "\n"+
			"			}" + "\n"+
			"		});" + "\n"+
			"	}" + "\n"+
			"});" + "\n"+
			"\n"+
			"Ext.onReady(function() {" + "\n"+
			"	/*" + "\n"+
			"	 * 針對 session 過期先作判斷, 若過期則無需跟 server 要資料." + "\n"+
			"	 * 例如在此頁 logout 後, 回上一頁(此頁)會掛掉" + "\n"+
			"	 * 所以利用 check session, 若過期則導回 logon 頁面" + "\n"+
			"	 * ezScrumContent(Summary), ViewList, ezScrumUserManagementUI 都先暫時用此方法" + "\n"+
			"	 * note: 若在 before render event 時 check session, 還是會先有 init 的動作, 會浪費資源" + "\n"+
			"	 */" + "\n"+
			"	checkUserSession();" + "\n"+
			"	" + "\n"+
			"	Ext.QuickTips.init();" + "\n"+
			"	" + "\n"+
			"	var Content = new ezScrumProjectList();" + "\n"+
			"	Content.render('content');" + "\n"+
			"	listenSessionForProjectList();	" + "\n"+
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