<%@ page contentType="text/html; charset=utf-8"%>

<html>
<head>
	<title>ezScrum, SSLab NTUT</title>
	<link rel="shortcut icon" href="images/scrum_16.png"/>
</head>

<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all-debug.js"></script>

<!--ezScrum team design tools  -->
<script type="text/javascript" src="javascript/ezScrumJSTool.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>

<script type="text/javascript" src="javascript/ux/gridfilters/menu/RangeMenu.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/menu/ListMenu.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CommonFormVTypes.js"></script>

<script type="text/javascript" src="javascript/ezScrumLayout/ezScrumLayoutSupport.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/CreateProjectWindow.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/Top_Panel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/Footer_Panel.js"></script>

<!--check session  -->
<script type="text/javascript" src="javascript/ezScrumPage/ValidateUserEvent.js"></script>

<link rel="stylesheet" type="text/css" href="css/Message.css"/>
<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/GridFilters.css" />
<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/RangeMenu.css" />
<link rel="stylesheet" type="text/css" href="css/ezScrum/TopPanel.css" />

<script type="text/javascript">
Ext.ns('ezScrum.window');

var Project = Ext.data.Record.create(['ID', 'Name', 'Comment', 'ProjectManager', 'CreateDate', 'DemoDate']);

var ProjectReader = new Ext.data.XmlReader({
	   record: 'Project',
	   idPath : 'ID',
	   successProperty: 'Result'
}, Project);

var ProjectStore = new Ext.data.Store({
	fields:[
		{name : 'ID'},
		{name : 'Name'},
		{name : 'Comment'},
		{name : 'ProjectManager'},
		{name : 'CreateDate'},
		{name : 'DemoDate'}
	],
	reader : ProjectReader
});

var CheckCreateProject = Ext.data.Record.create(['Check']);

var CheckReader = new Ext.data.XmlReader({
	record: 'CheckCreateProject'
}, CheckCreateProject);

var CheckStore = new Ext.data.Store({
	fields:[
		{name : 'Check'}
	],
	reader: CheckReader
});

var CreateProjectWindow = new ezScrum.window.CreateProjectWindow({
	listeners: {
		CreateSuccess:function(win, form, projectID){			
	 		this.hide();
	 		alert('Create success. Redirect to view project summary.');
	 		document.location.href = "./viewProject.do?PID=" + projectID;
		},
		CreateFailure:function(win, form, response){
			// Create Project Error
			alert("Create project on ITS failed. Please check the information of ITS and Project");
		},
		// 資料庫需要進行初始化的動作
		InitDatabase:function(win,form,response)
		{
			// Show a dialog using config options:
			Ext.Msg.show({
			   title:'The Database need be initialized',
			   msg: 'The Database <b>'+response.dbName+'</b> at <b>'+response.ip+'</b> will be initiated , and all data of the database will be clean',
			   buttons: Ext.Msg.YESNO,
			   fn: function(btn){
					// 如果同意那就呼叫InitialDB的Action
					if(btn == 'yes')
					{
						Ext.Ajax.request( {
							url : 'initialDatabase.do',
							success : function(response) {
								Ext.MessageBox.alert("Initial DataBase",'Initial DataBase Success, press Submit to continue.');
							},
							failure : function(response) {
								Ext.MessageBox.alert("Initial DataBase",'Initial DataBase Failure.');
							},
							params : form.getValues()
						});
					}
			},
			   icon: Ext.MessageBox.WARNING
			});	
		} ,
        CreateDatabase:function(win,form,response)
        {
            // Show a dialog using config options:
            Ext.Msg.show({
               title:'The Database need be created',
               msg: 'The Database <b>'+response.dbName+'</b> at <b>'+response.ip+'</b> will be created.',
               buttons: Ext.Msg.YESNO,
               fn: function(btn){
                    // 如果同意那就呼叫InitialDB的Action
                    if(btn == 'yes')
                    {
                        Ext.Ajax.request( {
                            url : 'createDatabase.do',
                            success : function(response) {
                                Ext.MessageBox.alert("Success!",'The Database has be created，please click the submit to next step ');
                            },
                            failure : function(response) {
                                alert("Create Database Falilure");
                            },
                            params : form.getValues()
                        });
                    }
               },
               icon: Ext.MessageBox.WARNING
            }); 
        }
	}
});

var ProjectsGird = new Ext.grid.GridPanel({
	id 			: 'Projects_GirdPanel',
	title		: 'Project List',
	region		: 'center',
    collapsible	: false,
	border		: false,
	frame		: false,
    stripeRows	: true,
	store		: ProjectStore,
    tbar		: [],
	viewConfig	: {
        forceFit: true
    },
	colModel: new Ext.grid.ColumnModel({
		columns: [
			{dataIndex: 'Name',header: 'Name', width: 100},
            {dataIndex: 'Comment',header: 'Comment', width: 150},
            {dataIndex: 'ProjectManager',header: 'Project Manager', width: 100},
            {dataIndex: 'CreateDate',header: 'Create Date', width: 100},
            {dataIndex: 'DemoDate',header: 'Demo Date', width: 100}
		]
	}),
	sm: new Ext.grid.RowSelectionModel({
    	singleSelect:true
    })
});

ProjectsGird.getSelectionModel().on({'selectionchange':{buffer:10, fn:function(){
	var single = this.getCount() == 1;
	if(single) {
		var record = this.getSelected();
		replaceURL( "./viewProject.do?PID=" + record.data['ID'] );
		//document.location.href = "./viewProject.do?PID=" + record.data['ID'];
	}
}}});

ezScrumProjectList = Ext.extend(Ext.Viewport, {
	id: 'ProjectListContentLayout',
	layout: 'border',
	initComponent: function() {
        this.items = [
			ezScrum.Project_TopPanel,
			ProjectsGird,
			ezScrum.FooterPanel
        ];
        
        ezScrumProjectList.superclass.initComponent.call(this);
    },
	listeners:{
		beforerender:function() {
			if (${CreateProject}) {
				var tbar = ProjectsGird.getTopToolbar();
				var btn = tbar.add({
					id:'createProjectBtn', 
					text:'Create Project', 
					icon:'images/add3.png',
					handler: function() {
						CreateProjectWindow.showWidget();
					}
				});
			}
		},
		render: function() {
			this.loadDataModel();
		}
	},
	loadDataModel: function() {
		var obj = this;
		var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.show();
		Ext.Ajax.request({
			url:'viewProjectList.do',
			success : function(response) {
				ProjectStore.loadData(response.responseXML);
				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
				loadmask.hide();
			},
			failure : function(){
				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
    			loadmask.hide();
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	}
});

Ext.onReady(function() {
	/*
	 * 針對 session 過期先作判斷, 若過期則無需跟 server 要資料.
	 * 例如在此頁 logout 後, 回上一頁(此頁)會掛掉
	 * 所以利用 check session, 若過期則導回 logon 頁面
	 * ezScrumContent(Summary), ViewList, ezScrumUserManagementUI 都先暫時用此方法
	 * note: 若在 before render event 時 check session, 還是會先有 init 的動作, 會浪費資源
	 */
	checkUserSession();
	
	Ext.QuickTips.init();
	
	var Content = new ezScrumProjectList();
	Content.render('content');
	listenSessionForProjectList();	
});
</script>

<div id="content"></div>
</html>