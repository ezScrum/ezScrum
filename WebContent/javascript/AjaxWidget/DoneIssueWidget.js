Ext.ns('ezScrum');

var IssueStore = new Ext.data.Store({
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

var ActualHour = new Ext.form.NumberField({
	fieldLabel	: 'Actual Hour',
	name      	: 'Actualhour',
	anchor		: '96%'                    
});

var CODate = new Ext.form.DateField({
	allowBlank	: true,
	fieldLabel	: 'Specific Checked Out Time',
	name		: 'ChangeDate',
	format		: 'Y/m/d-H:i:s',
	anchor		: '96%'
});

/* Check out Issue Form */
ezScrum.DoneForm = Ext.extend(Ext.form.FormPanel, {
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
            url     : 'doneIssue.do',
            loadUrl : 'showCheckOutIssue.do',
            items   : 
            		[{
                        fieldLabel	: 'ID',
                        name      	: 'Id',
                        readOnly	: true,
                        width 		: '95%'
                    },
                    {
                        fieldLabel	: 'Name',
                        name      	: 'Name',
                        readOnly	: true,
                        width 		: '95%'
                    },
                    ActualHour,
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
                        text     : 'Done',
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
        ezScrum.CheckOutForm.superclass.initComponent.apply(this, arguments);

        this.addEvents('DOSuccess', 'DOFailure', 'LoadIssueFailure');
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
    		this.fireEvent('DOSuccess', this, response);
    	}
    },
    onEditFailure: function(response) {
        this.fireEvent('DOFailure', this, response);
    },
    onLoadSuccess: function(response) {
    	ConfirmWidget.loadData(response);
    	if (ConfirmWidget.confirmAction()) {
			IssueStore.loadData(Ext.decode(response.responseText));		// load issue info
			var record = IssueStore.getAt(0);
			if(record) {
				this.getForm().setValues({
					Id: record.data['Id'],
					Name: record.data['Name'], 
					Partners: record.data['Partners'], 
					Notes: record.data['Notes'],
					Actualhour: 0
				});
				
				//this.fireEvent('LoadSuccess', this, response, record);
			}
    	}
    },
    onLoadFailure: function(response) {
        this.fireEvent('LoadIssueFailure', this, response);
    },
    reset: function() {
        this.getForm().reset();
    },
    loadIssue: function(id) {
        var obj = this;
        
    	Ext.Ajax.request({
			url: this.loadUrl,
			success: function(response) { obj.onLoadSuccess(response); },
			failure: function(response) { obj.onLoadFailure(response); },
			params : {issueID : id}
		});
    }
});

Ext.reg('ShowIssueDoneForm', ezScrum.DoneForm);

ezScrum.DoneIssueWidget = Ext.extend(Ext.Window, {
	title	: 'Done Issue',
	width	: 600,
	constrain:true,
	modal:true,
	closeAction	: 'hide',
	initComponent : function() {
		var config = {
			layout : 'form',
			items  : [{	xtype : 'ShowIssueDoneForm'	}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.DoneIssueWidget.superclass.initComponent.apply(this, arguments);

		this.addEvents('DoneSuccess', 'DoneFailure', 'LoadFailure');		
		this.items.get(0).on('DOSuccess', function(obj, response) { this.fireEvent('DoneSuccess', this, response); }, this);
		this.items.get(0).on('DOFailure', function(obj, response) { this.fireEvent('DoneFailure', this, response); }, this);
		this.items.get(0).on('LoadIssueFailure', function(obj, response) { this.fireEvent('LoadFailure', this, response); }, this);
	},
	showWidget : function(taskID) {
		this.items.get(0).reset();
		this.items.get(0).loadIssue(taskID);
		this.show();
	}
});