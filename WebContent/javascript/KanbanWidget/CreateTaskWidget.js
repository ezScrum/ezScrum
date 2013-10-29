Ext.ns('ezScrum');


/* Type Data */
var typeStoreForTask = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:typeReader
});

/* Handler Data */
var handlerStoreForTask = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:handlerReader
});

/* Type ComboBox */
var typeComboForTask = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: typeStoreForTask,
    name: 'TypeComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Type',
    id: 'TypeComboBoxForTask'
});

/* Handler ComboBox */
var handlerComboForTask = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: handlerStoreForTask,
    name: 'HandlerComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Handler',
    id: 'HandlerComboBoxForTask'
});

/* Create Task Form */
ezScrum.CreateTaskForm = Ext.extend(Ext.form.FormPanel, {
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
		        typeComboForTask,
		        handlerComboForTask
		        , {
		            fieldLabel: 'Size',
		            xtype: 'numberfield',
		            name: 'Size',
		            maxLength: 4
		        }, {
		        	fieldLabel: 'Description',
		            xtype: 'textarea',
		            name: 'Description',
		            height:200
		        }, {
		            name: 'PriorityComboBox',
		            text: '',
		            hidden: true
		        }, {
		            name: 'Deadline',
		            text: '',
		            hidden: true
		        }, {
		            name: 'issueTypeID',
		            hidden: true
		        }, {
		            name: 'parentID',
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
		ezScrum.CreateTaskForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
	},
	onRender:function() {
		ezScrum.CreateTaskForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var form = this.getForm();
		
		// ?? Disable ?霈?Action ?
		typeComboForTask.enable();
		
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
				this.fireEvent('CreateSuccess', this, response, record, typeComboForTask.originalValue);
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

Ext.reg('createTaskForm', ezScrum.CreateTaskForm);

ezScrum.AddNewTaskWidget = Ext.extend(Ext.Window, {
	title:'Add New Task',
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'createTaskForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddNewTaskWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
		
		this.items.get(0).on('CreateSuccess', function(obj, response, record, workitemType){ this.fireEvent('CreateSuccess', this, obj, response, record, workitemType); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response){ this.fireEvent('CreateFailure', this, obj, response, issueId); }, this);
	},
	showWidget:function(issueTypeID, workitemType, parentID){
		typeComboForTask.originalValue = workitemType;
		typeComboForTask.disable();
		
		this.items.get(0).reset();
		this.items.get(0).getForm().setValues({issueTypeID : issueTypeID, parentID : parentID});
		this.show();
	}
});