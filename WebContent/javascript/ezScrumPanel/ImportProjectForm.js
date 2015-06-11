Ext.ns('ezScrum');
Ext.ns('ezScrum.window');

ezScrum.ImportProjectForm = Ext.extend(Ext.form.FormPanel, {
	id: 'ImportProject_Form',
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 100,
	defaults: {
        width: 500,
        msgTarget: 'side'
    },
    monitorValid:true,
	initComponent:function() {
		var config = {
			verify_import_url: 'verifyImportProject.do',
			url : 'importProject.do',
			fileUpload : true,
			items: [{
					xtype:'fileuploadfield',
					emptyText: 'Select a file',
		            fieldLabel: 'Update File',
		            name: 'file',
		            vtype: 'checkUploadFileName',
		            allowBlank: false
			}],
		    buttons: 
		    [{
		    	formBind:true,
		    	id: 'SubmitBtn',
	    		text: 'Import',
	    		scope:this,
	    		handler: this.verify,
	    		disabled:true
	    	}]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		
		ezScrum.ImportProjectForm.superclass.initComponent.apply(this, arguments);
	},
	onRender:function() {
		ezScrum.ImportProjectForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	verify: function()
	{
		var obj = this;
		Ext.Ajax.request({
			url: obj.verify_import_url,
			success: function(response) {
				var result = Ext.decode(response.responseText);
				if (result.isDatabaseVersionValid) {
					obj.import();
				} else {
					Ext.Msg.show({
						   title:'View Report?',
						   msg: 'Import project fail due to database version, do you want to view report?',
						   buttons: Ext.Msg.YESNO,
						   fn: function(button) {
							   if (button == 'yes') {
								   Ext.Msg.show({
									   title:'Database Version Error',
									   msg: result.report,
									   buttons: Ext.Msg.OK,
									   animEl: 'elId',
									   icon: Ext.MessageBox.ERROR
									});
							   } 
						   },
						   animEl: 'elId',
						   icon: Ext.MessageBox.QUESTION
						});
				}
			},
			failure: function(response) {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	import: function()
	{
		if(this.getForm().isValid()) {
			var obj = this;
			this.getForm().submit({
				url:this.url,
				success: function(form, action) {
			        obj.onSuccess(action.response);
			    },
			    failure:function(form, action){
			    	obj.onFailure(action.response);
			    }
			});
		}
	},
	onSuccess:function(response) 
	{
		var result = Ext.decode(response.responseText);
		if(result.success) {
			Ext.example.msg('Import Success', 'Import Project Success');
		} else {
			this.onFailure(onFailure);
		}
	},
	onFailure:function(response) 
	{
    	var result = Ext.decode(response.responseText);
    	var newMessage = result.message.replace(";", "</br>");
    	Ext.Msg.show({
			   title:'Import Failure',
			   msg: newMessage,
			   buttons: Ext.Msg.OK,
			   animEl: 'elId',
			   icon: Ext.MessageBox.ERROR
			});
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('ImportProjectForm', ezScrum.ImportProjectForm);