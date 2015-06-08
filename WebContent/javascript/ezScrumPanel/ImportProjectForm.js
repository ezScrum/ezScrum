ImportProjectFormLayout = Ext.extend(Ext.form.FormPanel, {
	id 				: 'ImportProject_Form',
	border			: false,
	frame			: true,
	title			: 'Import Project',
	bodyStyle		: 'padding:0px',
	labelAlign		: 'right',
	labelWidth		: 150,
	buttonAlign		: 'left',
	monitorValid	: true,
	initComponent	: function() {
		var config = {
			url			: 'importProject.do',
			items		: [{
				xtype:'fileuploadfield',
				emptyText: 'Select a file',
	            fieldLabel: 'Update File',
	            name: 'file',
	            vtype: 'checkUploadFileName',
	            allowBlank: false
			}],
	        buttons : [{
	        	formBind : true,
	        	scope    : this,
	        	text     : 'Import',
	        	disabled : true,
	        	handler  : this.doImport
	        }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ImportProjectFormLayout.superclass.initComponent.apply(this, arguments);
	},
	showMask : function(msg) {
		this.loadmask = new Ext.LoadMask(this.getEl(), {msg: msg});
		this.loadmask.show();
	},
	closeMask : function() {
		this.loadmask.hide();
	},
    doImport: function() {
		var obj = this;
		var form = this.getForm();
		if(form.isValid()) {
			console.log('form.isValid()');
			console.log(form);
			form.submit({
				url:this.url,
				success: function(form, action) {
//			       obj.onSuccess(action);
			       console.log('success');
			    },
			    failure:function(form, action) {
			    	console.log('failure');
			    }
			});
		}
	},
//	doImport: function() {
//		var obj = this;
//		Ext.Ajax.request({
//			url: obj.url,
//			success: function(response) {
//				console.log('success');
//				var result = Ext.decode(response.responseText);
//				if (result.isDatabaseVersionValid) {
//					obj.exportProjects();
//				} else {
//					
//				}
//			},
//			failure: function(response) {
//				console.log('failure');
//				obj.closeMask();
//				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
//			}
//		});
//	},
});

Ext.reg('ImportProjectForm', ImportProjectFormLayout);