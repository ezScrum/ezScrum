Ext.ns('ezScrum');

/* is public ComboBox */
var publicCombo = new Ext.form.ComboBox({
    typeAhead: true,
    editable: false,
    mode: 'local',
    triggerAction: 'all',
    lazyRender: true,
    store: new Ext.data.ArrayStore({
        id: 0,
        fields: [
            'displayText'
        ],
        data: [['true'], ['false']]
    }),
    name: 'PublicComboBox',
    valueField: 'displayText',
    displayField: 'displayText',
    fieldLabel: 'Public',
    id: 'publicCombo',
    allowBlank: false
});

ezScrum.CreateIssueTypeForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 150,
	defaults: {
        width: 450,
        msgTarget: 'side'
    },
    monitorValid:true,
	initComponent:function() {
		var config = {
			url : 'ajaxAjaxAddIssueType.do',
			items: [
				{
		            fieldLabel: 'Issue Type Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 20
		        },
				publicCombo
		    ],
		    buttons: 
		    [{
		    	formBind:true,
	    		text: 'Submit',
	    		scope:this,
	    		handler: this.submit,
	    		disabled:true
	    	},
	        {
	        	text: 'Cancel',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CreateIssueTypeForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
	},
	onRender:function() {
		ezScrum.CreateIssueTypeForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var form = this.getForm();
		var obj = this;
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onSuccess(response);},
			failure:function(response){obj.onFailure(response);},
			params:form.getValues()
		});
	},
	onSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		this.fireEvent('CreateSuccess', this, response);
		myMask.hide();
	},
	onFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('CreateFailure', this, response);
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('CreateIssueTypeForm', ezScrum.CreateIssueTypeForm);

ezScrum.CreateIssueTypeWidget = Ext.extend(Ext.Window, {
	title:'Create New Issue Type',
	id:'te',
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'CreateIssueTypeForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CreateIssueTypeWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
		
		this.items.get(0).on('CreateSuccess', function(obj, response){ this.fireEvent('CreateSuccess', this, obj, response); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response){ this.fireEvent('CreateFailure', this, obj, response); }, this);
	},
	showWidget:function(sprint){
		this.items.get(0).reset();
		this.show();
	}
});
