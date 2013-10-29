Ext.ns('ezScrum');

var TaskStore = new Ext.data.Store({
	idIndex	: 0,
	id		: 0,
	fields	:[
		{name : 'Id'},
		{name : 'Name'},
		{name : 'Notes'},
		{name : 'Partners'}
		],
	reader	: taskJSReader
});

var CODate = new Ext.form.DateField({
	allowBlank	: true,
	fieldLabel	: 'Specific Checked Out Time',
	name		: 'ChangeDate',
	format		: 'Y/m/d-H:i:s',
	anchor		: '96%'
});

/* Check out Issue Form */
ezScrum.ReCheckOutForm = Ext.extend(Ext.form.FormPanel, {
    bodyStyle     : 'padding:15px',
    border        : false,
    defaultType   : 'textfield',
    labelAlign    : 'right',
    labelWidth    : 100,
    defaults      : {
        width     : 450,
        msgTarget : 'side'
    },
    monitorValid  : true,
    initComponent : function() {
        var config = {
            url     : 'resetTask.do',
            loadUrl : 'showCheckOutIssue.do',
            items   : 
            		[{
                        fieldLabel	: 'ID',
                        name      	: 'Id',
						readOnly	: true,
                        width 		: '95%'
                    },
                    {
                        fieldLabel	: 'Task Name',
                        name      	: 'Name',
                        width 		: '95%'
                    },
                    { 
	                    fieldLabel	: 'Partners',
                        name      	: 'Partners',
                        width 		: '95%'
					},
                    {
                        fieldLabel	: 'Notes',
                        xtype     	: 'textarea',
                        name      	: 'Notes',
                        width 		: '95%',
                        height    	: 150
                    },
                    CODate
                    ],
            buttons : 
            		[{
						formBind : true,
                        text     : 'Reset Check Out',
                        scope    : this,
                        handler  : this.submit,
                        disabled : true
                    }, {
                        text    : 'Cancel',
                        scope   : this,
                        handler : function() {	this.ownerCt.hide();  }
                    }]
        }

        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.ReCheckOutForm.superclass.initComponent.apply(this, arguments);

        this.addEvents('RECOSuccess', 'RECOFailure', 'LoadTaskFailure');
    },
    submit: function() {
        var form = this.getForm();
        var obj = this;
        
        Ext.Ajax.request({
			url     : this.url,
			params  : form.getValues(),
			success : function(response) {
				obj.onEditSuccess(response);
			},
			failure : function(response) {
				obj.onEditFailure(response);
			}		
		});
    },
    onEditSuccess: function(response) {
    	ConfirmWidget.loadData(response);
    	if (ConfirmWidget.confirmAction()) {
    		this.fireEvent('RECOSuccess', this, response);
    	}
    },
    onEditFailure: function(response) {
        this.fireEvent('RECOFailure', this, response);
    },
    onLoadSuccess: function(response) {
    	ConfirmWidget.loadData(response);
    	if (ConfirmWidget.confirmAction()) {
			TaskStore.loadData(Ext.decode(response.responseText));	// load task info
			handlerComboStoreForCO.load();							// load handler combo box value
				
			var record = TaskStore.getAt(0);
			if(record) {
				this.getForm().setValues({
					Id: record.data['Id'],
					Name: record.data['Name'], 
					Partners: record.data['Partners'], 
					Notes: record.data['Notes'] 
				});
				
				// this.fireEvent('LoadSuccess', this, response, record);
			}
    	}
    },
    onLoadFailure: function(response) {
        this.fireEvent('LoadTaskFailure', this, response);
    },
    reset: function() {
        this.getForm().reset();
    },
    loadTask: function(id) {
        var obj = this;
        
    	Ext.Ajax.request({
			url: this.loadUrl,
			success: function(response) { obj.onLoadSuccess(response); },
			failure: function(response) { obj.onLoadFailure(response); },
			params : {issueID : id}
		});
    }
});

Ext.reg('ResetCheckOutTaskForm', ezScrum.ReCheckOutForm);

ezScrum.ReCheckOutWidget = Ext.extend(Ext.Window, {
	title	: 'Reset Checked Out Task',
	width	: 600,
	constrain:true,
	modal:true,
	closeAction	: 'hide',
	initComponent : function() {
		var config = {
			layout : 'form',
			items  : [{	xtype : 'ResetCheckOutTaskForm'	}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CheckOutWidget.superclass.initComponent.apply(this, arguments);

		this.addEvents('RECheckOutSuccess', 'RECheckOutFailure', 'LoadFailure');		
		this.items.get(0).on('RECOSuccess', function(obj, response) { this.fireEvent('RECheckOutSuccess', this, response); }, this);
		this.items.get(0).on('RECOFailure', function(obj, response) { this.fireEvent('RECheckOutFailure', this, response); }, this);
		this.items.get(0).on('LoadTaskFailure', function(obj, response) { this.fireEvent('LoadFailure', this, response); }, this);
	},
	showWidget : function(taskID) {
		this.items.get(0).reset();
		this.items.get(0).loadTask(taskID);
		this.show();
	}
});