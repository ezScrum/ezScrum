// the form is for Summary Page
ProjectDescForm = Ext.extend(ezScrum.layout.InfoForm, {
	id			: 'ProjectDesc',
	title		: 'Project Description',
    store		: ProjectDescStore,
	initComponent : function() {
		var config = {
			url		: 'GetProjectDescription.do',
			items	: [ ProjectDescItem ]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ProjectDescForm.superclass.initComponent.apply(this, arguments);
	},
	loadDataModel: function() {
		var obj = this;
		Ext.Ajax.request({
			url : obj.url,
			success: function(response) {
	    		ProjectDescStore.loadData(Ext.decode(response.responseText));
	    		var record = ProjectDescStore.getAt(0);
    			obj.setDataModel(record);
			},
			failure: function(response) {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	setDataModel: function(record) {
    	var replaced_comment = replaceJsonSpecialChar(record.get('Commnet'));
    	var replaced_projectManager = replaceJsonSpecialChar(record.get('ProjectManager'));
		
		this.getForm().setValues({
			Commnet: replaced_comment,
			ProjectManager: replaced_projectManager, 
			ProjectCreateDate: record.get('ProjectCreateDate')
		});
	}
});
Ext.reg('ProjectDescForm', ProjectDescForm);

// the form is for Modify Config Page
ProjectModifyForm = Ext.extend(ezScrum.layout.InfoForm, {
	id			: 'ProjectDescModify',
	title		: 'Project Preference',
	buttonAlign	: 'left',
	store		: ProjectModifyStore,
	initComponent : function() {
		var config = {
			url			: 'GetProjectDescription.do',
			modify_url	: 'ModifyProjectDescription.do',	
			items 		: [ ProjectDescModifyItem ],
	        buttons : [{
	        	text     : 'Modify It',
	        	scope    : this,
	        	handler  : this.doModify,
	        	disabled : false
	        }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ProjectModifyForm.superclass.initComponent.apply(this, arguments);
	},
    loadDataModel: function() {
    	var obj = this;
    	MainLoadMaskShow();
    	Ext.Ajax.request({
    		url : obj.url,
    		success: function(response) {
    			ConfirmWidget.loadData(response);
    			if (ConfirmWidget.confirmAction()) {
    				ProjectDescStore.loadData(Ext.decode(response.responseText));
    				var record = ProjectDescStore.getAt(0);
					obj.setDataModel(record);
					
					MainLoadMaskHide();
    			}
    		},
    		failure: function(response) {
    			MainLoadMaskHide();
    			
    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    		}
    	});
    },
    setDataModel: function(record) {
    	var replaced_projectName = replaceJsonSpecialChar(record.get('ProjectName'));
    	var replaced_displayprojectName = replaceJsonSpecialChar(record.get('ProjectDisplayName'));
    	var replaced_comment = replaceJsonSpecialChar(record.get('Commnet'));
    	var replaced_projectManager = replaceJsonSpecialChar(record.get('ProjectManager'));
    	
    	this.getForm().setValues({
    		ProjectName: replaced_projectName,
    		ProjectDisplayName: replaced_displayprojectName,
    		Commnet: replaced_comment,
    		ProjectManager: replaced_projectManager, 
    		AttachFileSize: record.get('AttachFileSize')
    	});
    },
    doModify: function() {
		var obj = this;
    	var form = this.getForm();
    	var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		loadmask.show();
    	Ext.Ajax.request({
    		url: obj.modify_url,
    		params: form.getValues(),
    		success: function(response) {
    			ConfirmWidget.loadData(response);
    			if (ConfirmWidget.confirmAction()) {
    				var result = response.responseText;
    				if (result == "success") {
    					Ext.example.msg('Modify Project', 'Success.');
    				} else {
    					Ext.example.msg('Modify Project', 'Sorry, the action is failure.');
    				}
    				var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
    				loadmask.hide();
    			}
    		},
    		failure:function(response){
    			var loadmask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
				loadmask.hide();
    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    		}
    	});
	}
});
Ext.reg('ProjectModifyForm', ProjectModifyForm);