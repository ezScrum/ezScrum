var myCheckboxGroup = new Ext.form.CheckboxGroup({
    id:'myGroup',
    xtype: 'checkboxgroup',
    fieldLabel: 'Existing Project List',
    itemCls: 'x-check-group-alt',
    // Put all controls in a single column with width 100%
    columns: 1,
    items: [
            {boxLabel: 'Item 1', name: 'cb-col-1'},
            {boxLabel: 'Item 2', name: 'cb-col-2', checked: true},
            {boxLabel: 'Item 3', name: 'cb-col-3'}
        ]
});

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
	title			: 'Export Project',
	bodyStyle		: 'padding:0px',
	labelAlign		: 'right',
	labelWidth		: 150,
	buttonAlign		: 'left',
	monitorValid	: true,
	initComponent	: function() {
		var config = {
			url			: 'viewProjectList.do',
			export_url	: 'exportProject.do',	
			items		: myCheckboxGroup,
	        buttons : [{
	        	formBind : true,
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
	loadDataModel: function() {
		var obj = this;
		var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.show();
		Ext.Ajax.request({
			url: obj.url,
			success : function(response) {
				ProjectStore.loadData(response.responseXML);
				for (i = 0; i < ProjectStore.getCount(); i++) {
					var record = ProjectStore.getAt(i);
//					console.log(record);
					myCheckboxGroup.items.add(new Ext.form.Checkbox({boxLabel: record.get('Name'), name: 'cb-col-4'}));
		    	}
				console.log(myCheckboxGroup);
				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
				loadmask.hide();
				obj.doLayout();
			},
			failure : function(){
				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
    			loadmask.hide();
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
    setDataModel: function(record) {
    	this.getForm().reset();
    	this.getForm().setValues({
			ServerUrl	: record.get('ServerUrl'),
			DBAccount	: record.get('DBAccount'),
			DBType		: record.get('DBType'),
			DBName		: record.get('DBName')
		});
    },
    doExport: function() {
		var obj = this;
    	var form = this.getForm();
    	this.showMask('loading info...');
    	var projectsName = "";
    	for (i = 1; i <= 2; i++) {
    		projectsName += "Project" + i + ",";
    	}
    	console.log(projectsName);
    	window.open('http://127.0.0.1:8080/ezScrum/exportProject.do?projectsName=' + projectsName);
    	var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
    	Ext.example.msg('Success', 'Export Project Success');
		loadmask.hide();
	}
});

Ext.reg('ExportProjectForm', ExportProjectFormLayout);