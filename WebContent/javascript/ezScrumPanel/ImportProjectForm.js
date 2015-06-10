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
	    		handler: this.import,
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
		console.log(result);
		if(result.success) {
			Ext.example.msg('Import Success', 'Import Project Success');
		} else {
			this.onFailure(onFailure);
		}
	},
	onFailure:function(response) 
	{
    	var result = Ext.decode(response.responseText);
    	Ext.example.msg('Import Failure', result.message);
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('ImportProjectForm', ezScrum.ImportProjectForm);