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

ExportProjectFormLayout = Ext.extend(Ext.form.FormPanel, {
	id 				: 'ExportProject_Form',
	border			: false,
	frame			: true,
	store			: ProjectStore,
	title			: 'Select the projects you want to export:',
	bodyStyle		: 'padding:0px',
	labelAlign		: 'right',
	labelWidth		: 150,
	buttonAlign		: 'left',
	monitorValid	: true,
	initComponent	: function() {
		var config = {
			url			: 'viewProjectList.do',
			export_url	: 'exportProject.do',	
			verify_export_url: 'verifyExportProject.do',
			items		: [],
	        buttons : [{
	        	id: 'exportButton',
	        	scope    : this,
	        	text     : 'Export',
	        	disabled : true,
	        	handler  : this.doExport
	        }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ExportProjectFormLayout.superclass.initComponent.apply(this, arguments);
	},
	showMask : function(msg) {
		this.loadmask = new Ext.LoadMask(this.getEl(), {msg: msg});
		this.loadmask.show();
	},
	closeMask : function() {
		this.loadmask.hide();
	},
	updateButtonStatus : function() {
		var exportButton = Ext.getCmp('exportButton');
		var isValid = false;
		for (i = 0; i < ProjectStore.getCount(); i++) {
			var checkBox = Ext.getCmp(ProjectStore.getAt(i).get('ID'));
			if (checkBox.checked) {
				isValid = true;
				break;
			}
		}
		if (isValid) {
			exportButton.enable();
		} else {
			exportButton.disable();
		}
	},
	loadDataModel: function() {
		var obj = this;
		this.showMask('loading info...');
		Ext.Ajax.request({
			url: obj.url,
			success : function(response) {
				ProjectStore.loadData(response.responseXML);
				for (i = 0; i < ProjectStore.getCount(); i++) {
					var record = ProjectStore.getAt(i);
					obj.items.add(new Ext.form.Checkbox({
								id: record.get('ID'), 
								boxLabel: record.get('Name'), 
								listeners:{
									check: function() {
										obj.updateButtonStatus();
									}}}));
				}
			},
			failure : function(){
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
		this.closeMask();
	},
    doExport: function() {
		var obj = this;
		Ext.Ajax.request({
			url: obj.verify_export_url,
			success: function(response) {
				var result = Ext.decode(response.responseText);
				if (result.isSuccess) {
					obj.exportProjects();
				} else {
					Ext.example.msg('Export Failure', 'Database version error.');
				}
			},
			failure: function(response) {
				obj.closeMask();
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
		
	},
	exportProjects() {
		var projectsName = "";
    	for (i = 0; i < ProjectStore.getCount(); i++) {
			var checkBox = Ext.getCmp(ProjectStore.getAt(i).get('ID'));
			if (checkBox.checked) {
				projectsName += checkBox.id + ",";
			}
		}
    	var url = 'http://127.0.0.1:8080/ezScrum/exportProject.do?projectsName=' + projectsName;
    	this.download(url);
    	Ext.example.msg('Success', 'Export Project Success');
	},
	download(url) {
		var form = Ext.DomHelper.append(document.body, {
	        tag : 'form',
	        method : 'post',
	        action : url
	    });
	    form.submit();
	}
});

Ext.reg('ExportProjectForm', ExportProjectFormLayout);