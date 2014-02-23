var Modify_AccountStore = new Ext.data.Store({
	fields:[
	   	{name : 'Name'},
		{name : 'ID'},
		{name : 'Account'},
		{name : 'Mail'},
		{name : 'Roles'},
		{name : 'Enable'},
		{name : 'Password'}
	],
	reader : AccountReader
});

ezScrum.ModifyAccountPasswordForm = Ext.extend(Ext.form.FormPanel, {
	accountID	  : '-1',
	isEdit		  : false,
	notifyPanel	  : undefined,
	userRecord    : undefined,
    border        : false,
    monitorValid  : true,
    bodyStyle     : 'padding:15px',
    defaultType   : 'textfield',
    labelAlign    : 'right',
    labelWidth    : 100,
    store		  : Modify_AccountStore,
    defaults      : {
        width     : 350,
        msgTarget : 'side'
    },
    onRender : function() {
    	ezScrum.ModifyAccountPasswordForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
    initComponent : function() {
        var config = {
            url     : 'modifyAccount.do',
            loadUrl : 'showAccountInfo.do',
            items   : [
                { 
            		fieldLabel	: 'User ID',
	                name      	: 'account',
	                width 		: '95%',                                         
	                ref			: 'Edit_AccountPassword_UserID_refID',
	                regex : /^[\w-_()~ ]*$/,
	                regexText : 'Only a-z,A-Z,0-9,-,_,(,),~,space allowed.'
				}, { 
	                fieldLabel	: 'Password',
	                name      	: 'passwd',
	                width 		: '95%',
	                inputType   : 'password',
	                id          : 'Edit_AccountPassword_Form_Password',
	                ref			: 'Edit_AccountPassword_Password_refID',
	                allowBlank  : false
				}, { 
	                fieldLabel	: 'Re-enter',
	                name      	: 'reenter',
	                width 		: '95%',
	                inputType   : 'password',
	                initialPassField: 'Edit_AccountPassword_Form_Password',                       
	                vtype       : 'password',
	                ref			: 'Edit_AccountPassword_RePassword_refID',
	                allowBlank  : false
				}, {
					name		: 'isEdit',
					hidden		: true,
					ref			: 'Edit_AccountPassword_isEdit_refID'
				}, { 
	                name      	: 'id',
	                hidden		: 'true'
				}, {
	            	xtype      : 'RequireFieldLabel'
	            }],
			buttons : [{
					formBind : true,
                    text     : 'Save',
                    scope    : this,
                    handler  : this.doEvent
                }, {
                    text    : 'Cancel',
                    scope   : this,
                    handler : function() { this.ownerCt.hide(); }
                }
            ]
        }
   		Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.ModifyAccountPasswordForm.superclass.initComponent.apply(this, arguments);       
    },
    doEvent : function() {
		checkUserSession();//global的做法，不太好 但是目前框架只能允許這麼做
		this.Edit_AccountPassword_UserID_refID.enable();	// 特別 enable user ID 是因為 disable 會讓後端 action 取不到資料 
		this.doModify();
    },
    doModify : function() {
    	var userPassword = this.getForm().findField('passwd').getValue();
		Ext.Ajax.request({
        	scope	: this,
			url     : this.url,
			params  : {
						id      : this.getRecord().data['ID'],
						account	: this.getRecord().data['Account'],
						name    : this.getRecord().data['Name'],
						passwd  : userPassword,
						mail    : this.getRecord().data['Mail'],
						enable  : this.getRecord().data['Enable'],
						isEdit  : this.getForm().findField('isEdit').getValue()
					},
			success : function(response) { this.onModifySuccess(response); }	
		});
	},
	onModifySuccess : function(response) {
		var rs = AccountReader.readRecords(response.responseXML);
		if(rs.success) {
			var record = rs.records[0];
			if(record) {
				this.notifyPanel.notify_EditAccountPassword("true", record);
			}
		}
	},
    reset : function() {
        this.getForm().reset();
    },
    setRecord: function(record) {
    	this.userRecord = record;
    },
    getRecord: function() {
    	return this.userRecord;
    },
    initialEditForm: function(userID) {
    	this.Edit_AccountPassword_UserID_refID.disable();
    	this.Edit_AccountPassword_isEdit_refID.setValue(true);
    	this.Edit_AccountPassword_UserID_refID.setValue(userID);
    	this.accountID = userID;
    }
});
Ext.reg('Management_ModifyAccountPasswordForm', ezScrum.ModifyAccountPasswordForm);

ezScrum.window.ModifyAccountPasswordWindow = Ext.extend(ezScrum.layout.Window, {
    title       : 'Edit Account Password',
    width		: 600,
    bodyStyle	: 'padding: 5px',
    initComponent : function() {
        var config = {
            layout : 'form',
            items  : [{ xtype: 'Management_ModifyAccountPasswordForm', ref: 'Management_ModifyAccountPasswordForm_refID' }]
        }
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.window.ModifyAccountPasswordWindow.superclass.initComponent.apply(this, arguments);
    },
    showTheWindow_EditPassword: function(panel, record) {
    	// initial form info
    	var userID = record.data['Account'];
    	this.Management_ModifyAccountPasswordForm_refID.reset();
    	this.Management_ModifyAccountPasswordForm_refID.initialEditForm(userID);
    	this.Management_ModifyAccountPasswordForm_refID.notifyPanel = panel;
    	
    	// initial window info
    	this.Management_ModifyAccountPasswordForm_refID.setRecord(record);
    	this.setTitle('Change Password');
    	this.show();
    }
});

var Account_ModifyPassword_Window = new ezScrum.window.ModifyAccountPasswordWindow();