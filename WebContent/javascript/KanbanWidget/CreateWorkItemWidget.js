Ext.ns('ezScrum');

/* Status Data */
/*
var statusStoreForCreate = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:statusReader
});
*/
/* Status ComboBox */
/*
var statusComboForCreate = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: statusStoreForCreate,
    name: 'StatusComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Status',
    id: 'StatusComboBoxForCreate'
});
*/

/* Type Data */
var typeStoreForCreate = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:typeReader
});

/* Priority Data */
var priorityStoreForCreate = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:priorityReader
});

/* Handler Data */
var handlerStoreForCreate = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:handlerReader
});

/* Type ComboBox */
var typeComboForCreate = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: typeStoreForCreate,
    name: 'TypeComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Type',
    id: 'TypeComboBoxForCreate'
});

/* Priority ComboBox */
var priorityComboForCreate = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: priorityStoreForCreate,
    name: 'PriorityComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Priority',
    id: 'PriorityComboBoxForCreate'
});

/* Handler ComboBox */
var handlerComboForCreate = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: handlerStoreForCreate,
    name: 'HandlerComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Handler',
    id: 'HandlerComboBoxForCreate'
});

var deadlineForCreate = new Ext.form.DateField({
	fieldLabel: 'Deadline',
	xtype: 'datefield',
	name: 'Deadline',
	format: 'Y/m/d',
	altFormats: 'Y/m/d'
});

/* Create WorkItem Form */
ezScrum.CreateWorkItemForm = Ext.extend(Ext.form.FormPanel, {
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
			url : 'ajaxAddNewWorkItem.do',
			items: [{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 128
		        },
		        typeComboForCreate,
		        priorityComboForCreate,
		        handlerComboForCreate
		        , {
		            fieldLabel: 'Size',
		            xtype: 'numberfield',
		            name: 'Size',
		            maxLength: 4
		        },
		        deadlineForCreate
		        , {
		        	fieldLabel: 'Description',
		            xtype: 'textarea',
		            name: 'Description',
		            height:200
		        }, {
		            name: 'issueTypeID',
		            hidden: true
		        }
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
		ezScrum.CreateWorkItemForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
	},
	onRender:function() {
		ezScrum.CreateWorkItemForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var form = this.getForm();
		
		// ?? Disable ?霈?Action ?
		typeComboForCreate.enable();
		
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
		myMask.hide();
		var rs = jsonWorkItemReader.read(response);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('CreateSuccess', this, response, record, typeComboForCreate.originalValue);
			}

		}
		else
			this.fireEvent('CreateFailure', this, response);
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

Ext.reg('createWorkItemForm', ezScrum.CreateWorkItemForm);

ezScrum.AddNewWorkItemWidget = Ext.extend(Ext.Window, {
	title:'Add New WorkItem',
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'createWorkItemForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddNewWorkItemWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
		
		this.items.get(0).on('CreateSuccess', function(obj, response, record, workitemType){ this.fireEvent('CreateSuccess', this, obj, response, record, workitemType); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response){ this.fireEvent('CreateFailure', this, obj, response, issueId); }, this);
	},
	showWidget:function(issueTypeID, workitemType){
		var currentTime = new Date();
		var month = currentTime.getMonth() + 1;
		var day = currentTime.getDate();
		var year = currentTime.getFullYear();
		if (month.toString().length < 2)
			month = '0' + month;
		// deadline ?撠潛隞
		deadlineForCreate.setMinValue(year + '/' + month + '/' + day);
		
		typeComboForCreate.originalValue = workitemType;
		typeComboForCreate.disable();
		
		this.items.get(0).reset();
		this.items.get(0).getForm().setValues({issueTypeID : issueTypeID});
		this.show();
	}
});