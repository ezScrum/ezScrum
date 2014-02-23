var UserInformation_AccountStore = new Ext.data.Store({
	fields:[
	    {name : 'Name'},
		{name : 'ID'},
		{name : 'Account'},
		{name : 'Mail'},
		{name : 'Roles'},
		{name : 'Enable'},
	],
	reader : AccountReader
});

ezScrum.ModifyUserAccountPasswordForm = Ext.extend(Ext.form.FormPanel, {
	id			    : 'UserPassword_Management_Form',
    store		    : UserInformation_AccountStore,
    bodyStyle		: 'padding:50px',
    monitorValid	: true,
	autoHeight		: true,
	buttonAlign		: 'left',
    border        : false,
    defaultType   : 'textfield',
    labelWidth    : 100,
    defaults      : {
        msgTarget : 'side'
    },
    initComponent : function() {
        var config = {
        	getUserDataUrl	: 'getUserData.do',
        	modifyUrl	    : 'updateAccount.do' ,
            items   : [
                { 
            		fieldLabel	: 'User ID',
	                name      	: 'account',
	                width 		: '200',                                         
	                ref			: 'Edit_UserAccountPassword_UserID_refID',
	                regex : /^[\w-_()~ ]*$/,
	                regexText : 'Only a-z,A-Z,0-9,-,_,(,),~,space allowed.'
				}, { 
	                fieldLabel	: 'Password',
	                name      	: 'passwd',
	                width 		: '200',
	                inputType   : 'password',
	                id          : 'Edit_AccountPassword_Form_Password',
	                ref			: 'Edit_AccountPassword_Password_refID',
	                allowBlank  : false
				}, { 
	                fieldLabel	: 'Re-enter',
	                name      	: 'reenter',
	                width 		: '200',
	                inputType   : 'password',
	                initialPassField: 'Edit_AccountPassword_Form_Password',                       
	                vtype       : 'password',
	                ref			: 'Edit_AccountPassword_RePassword_refID',
	                allowBlank  : false
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
                    handler  : this.doEvent,
                }
            ]
        }
   		Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.ModifyUserAccountPasswordForm.superclass.initComponent.apply(this, arguments);       
    },
    doEvent : function() {
		checkUserSession();//global的做法，不太好 但是目前框架只能允許這麼做
		this.doModify();
    },
    doModify : function() {
    	var userPassword = this.getForm().findField('passwd').getValue();
		Ext.Ajax.request({
        	scope	: this,
			url     : this.modifyUrl,
			params  : {
						id		: this.getRecord().data['ID'],
						account	: this.getRecord().data['Account'],
						name	: this.getRecord().data['Name'],
						passwd	: userPassword,
						enable	: this.getRecord().data['Enable'],
						mail	: this.getRecord().data['Mail']
			},
			success : function(response) { 
				var rs = AccountReader.readRecords(response.responseXML);
				var title = "Change Password";
				if(rs.success) {
					Ext.example.msg(title, "Success.");
				}else{
					Ext.example.msg(title, "Sorry, please try again.");
				}
			}	
		});
	},
    setRecord: function(record) {
    	this.getForm().reset();
    	this.getForm().findField('account').setValue( record.data['Account'] );
    	this.getForm().findField('account').disable();
    	this.userRecord = record;
    },
    getRecord: function() {
    	return this.userRecord;
    },
    loadUserDataModel: function() {
		UserManagementMainLoadMaskShow();
		Ext.Ajax.request({
			scope	: this,
			url 	: this.getUserDataUrl,
			success: function(response) {
				UserInformation_AccountStore.loadData(response.responseXML);
				var record = UserInformation_AccountStore.getAt(0);
    			this.setRecord(record);
    			UserManagementMainLoadMaskHide();
			},
			failure: function(response) {
				UserManagementMainLoadMaskHide();
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
});
Ext.reg('Management_ModifyUserAccountPasswordForm', ezScrum.ModifyUserAccountPasswordForm);