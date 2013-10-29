Ext.ns('ezScrum');

var IssueStore = new Ext.data.Store( {
	idIndex : 0,
	id : 0,
	fields : [ {
		name : 'Id'
	}, {
		name : 'Name'
	}, {
		name : 'Notes'
	}, {
		name : 'Partners'
	} ],
	reader : taskJSReader
});

var CODate = new Ext.form.DateField( {
	allowBlank : true,
	fieldLabel : 'Specific Checked Out Time',
	name : 'ChangeDate',
	format : 'Y/m/d-H:i:s',
	anchor : '96%'
});

// reopen Issue Form
ezScrum.ReOpenForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle : 'padding:15px',
	border : false,
	defaultType : 'textfield',
	labelAlign : 'right',
	labelWidth : 100,
	defaults : {
		width : 450,
		msgTarget : 'side'
	},
	monitorValid : true,
	initComponent : function() {
		var config = {
			url : 'reopenIssue.do',
			loadUrl : 'showCheckOutIssue.do',
			items : [ {
				fieldLabel : 'ID',
				name : 'Id',
				readOnly : true,
				width : '95%'
			}, {
				fieldLabel : 'Name',
				name : 'Name',
				readOnly : true,
				width : '95%'
			}, {
				fieldLabel : 'Notes',
				xtype : 'textarea',
				name : 'Notes',
				width : '95%',
				height : 150
			}, CODate ],
			buttons : [ {
				formBind : true,
				text : 'Re Open',
				scope : this,
				handler : this.submit,
				disabled : true
			}, {
				text : 'Cancel',
				scope : this,
				handler : function() {
					this.ownerCt.hide();
				}
			} ]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CheckOutForm.superclass.initComponent.apply(this, arguments);

		this.addEvents('ReOSuccess', 'ReOFailure', 'LoadIssueFailure');
	},
	submit : function() {
		var form = this.getForm();
		var obj = this;

		Ext.Ajax.request( {
			url : this.url,
			params : form.getValues(),
			success : function(response) {
				obj.onEditSuccess(response);
			},
			failure : function(response) {
				obj.onEditFailure(response);
			}
		});
	},
	onEditSuccess : function(response) {
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			this.fireEvent('ReOSuccess', this, response);
		}
	},
	onEditFailure : function(response) {
		this.fireEvent('ReOFailure', this, response);
	},
	onLoadSuccess : function(response) {
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			IssueStore.loadData(Ext.decode(response.responseText)); // load issue info
			var record = IssueStore.getAt(0);
			if (record) {
				this.getForm().setValues( {
					Id : record.data['Id'],
					Name : record.data['Name'],
					Partners : record.data['Partners'],
					Notes : record.data['Notes']
				});

				// this.fireEvent('LoadSuccess', this, response, record);
			}
		}
	},
	onLoadFailure : function(response) {
		this.fireEvent('LoadIssueFailure', this, response);
	},
	reset : function() {
		this.getForm().reset();
	},
	loadIssue : function(id) {
		var obj = this;
		
		Ext.Ajax.request( {
			url : this.loadUrl,
			success : function(response) {
				obj.onLoadSuccess(response);
			},
			failure : function(response) {
				obj.onLoadFailure(response);
			},
			params : {
				issueID : id
			}
		});
	}
});

Ext.reg('ShowReOpenForm', ezScrum.ReOpenForm);

ezScrum.ReOpenIssueWidget = Ext.extend(Ext.Window, {
	title : 'Re Opened Issue',
	width : 600,
	closeAction : 'hide',
	constrain : true,
	modal : true,
	initComponent : function() {
		var config = {
			layout : 'form',
			items : [ {
				xtype : 'ShowReOpenForm'
			} ]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CheckOutWidget.superclass.initComponent.apply(this, arguments);

		this.addEvents('ReOpenSuccess', 'ReOpenFailure', 'LoadFailure');
		this.items.get(0).on('ReOSuccess', function(obj, response) {
			this.fireEvent('ReOpenSuccess', this, response);
		}, this);
		this.items.get(0).on('ReOFailure', function(obj, response) {
			this.fireEvent('ReOpenFailure', this, response);
		}, this);
		this.items.get(0).on('LoadIssueFailure', function(obj, response) {
			this.fireEvent('LoadFailure', this, response);
		}, this);
	},
	showWidget : function(taskID) {
		this.items.get(0).reset();
		this.items.get(0).loadIssue(taskID);
		this.show();
	}
});