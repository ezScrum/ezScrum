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

<script src="https://www.gstatic.com/firebasejs/3.9.0/firebase.js"></script>
<script src="javascript/promise.js"></script>

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
	 		document.location.href = "./viewProject.do?projectName=" + projectID;
		},
		CreateFailure:function(win, form, response){
			// Create Project Error
			alert("Create project on ITS failed. Please check the information of ITS and Project");
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
		replaceURL( "./viewProject.do?projectName=" + record.data['ID'] );
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
			var menuItems = [];
			var tbar = ProjectsGird.getTopToolbar();
			if (${CreateProject}) {
				var CreateProjectItem = {
						id:'createProjectBtn', 
						text:'Create Project', 
						icon:'images/add3.png',
						handler: function() {
							CreateProjectWindow.showWidget();
						}
				};
				menuItems.push(CreateProjectItem);
			}
			var SelectProject ={
					id: 'settingAttentionProject',
					text: 'Setting Attention Project',
					icon: 'images/Notification_setting.png',
					menu: {
						items: [],
						listeners: {
							itemclick: function(baseItem, e) {
								Ext.Ajax.request({
									url:'switchNotification.do',
									params:{
										event:"updateProjectSubscriptStatus",
										Id: baseItem.projectName,
										statusType: baseItem.statusType,
										status: !baseItem.checked
									},
									success:function(data){
										if(data.responseText != "Success"){
											alert(data.responseText);
											this.show;
										}
										
									}
								});								
							},
							show: function(menu) {
								Ext.getCmp('ProjectListContentLayout').loadProjectStatus();
							}
						}
					}		
			}
			menuItems.push(SelectProject);
			var btn = tbar.add(menuItems);
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
	},
	loadProjectStatus : function(){
		Ext.Ajax.request({
			url:'getProjectsSubscriptStatus.do',
			success : function(response){
				console.log()
				Ext.getCmp('ProjectListContentLayout').showProjectStatusMenu(response);
			}
		});
	},
	showProjectStatusMenu : function(record) {		
		projectStatusMenu = ProjectsGird.getTopToolbar().getComponent('settingAttentionProject');		
		projectStatusMenu.menu.removeAll();

		for ( var j = 0; j < ProjectStore.getCount(); j++) {
			//TODO For event.
			var projectRecord = ProjectStore.getAt(j);
			projectStatusMenu.menu.add({				
				projectName: projectRecord.data['ID'],
				text: projectRecord.data['Name'],
				statusType : "Project",
				xtype: 'menucheckitem',
				hideOnClick: false
			});
		}
		
		// set click items
		var projectsStatus = JSON.parse(record.responseText).ezScrum;
		projectStatusMenu.menu.items.each(function() {
			this.setChecked(false);
			for ( var i = 0; i < projectsStatus.length; i++) {
				if (projectsStatus[i] != "" && this.statusType == "Project"
						&& this.projectName == projectsStatus[i].Id) {
					this.setChecked(projectsStatus[i].Subscribe);
				}
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