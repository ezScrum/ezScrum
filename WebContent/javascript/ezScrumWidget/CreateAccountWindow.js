var Modify_AccountStore = new Ext.data.Store({
	fields:[
	   	{name : 'Name'},
		{name : 'ID'},
		{name : 'Mail'},
		{name : 'Roles'},
		{name : 'Enable'},
		{name : 'Password'}
	],
	reader : AccountReader
});

ezScrum.ModifyAccountForm = Ext.extend(Ext.form.FormPanel, {
	accountID	  : '-1',
	notifyPanel	  : undefined,
	
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
    	ezScrum.ModifyAccountForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
    initComponent : function() {
        var config = {
            url     : 'modifyAccount.do',
            loadUrl : 'showAccountInfo.do',
            items   : [{ 
	                fieldLabel	: 'User ID',
	                name      	: 'account',
	                width 		: '95%',                                         
	                allowBlank  : false,
	                ref			: 'Management_Account_UserID_refID',
	                regex : /^[\w-_()~ ]*$/,
	                regexText : 'Only a-z,A-Z,0-9,-,_,(,),~,space allowed.'
				}, {
	                fieldLabel	: 'User Name',
	                name      	: 'name',                        
	                width 		: '95%',                       
	                allowBlank  : false,
	        		regex : /^[^"'\\><&]*$/,
	        		regexText : 'deny following char " < > \\ & \''
	            }, { 
	                fieldLabel	: 'Password',
	                name      	: 'passwd',
	                width 		: '95%',
	                inputType   : 'password',
	                id          : 'Modify_Account_Form_Password',
	                allowBlank  : false,
	                ref			: 'Management_Account_Password_refID'
				}, { 
	                fieldLabel	: 'Re-enter',
	                name      	: 'reenter',
	                width 		: '95%',
	                inputType   : 'password',
	                allowBlank  : false,
	                initialPassField: 'Modify_Account_Form_Password',                       
	                vtype       : 'password',
	                ref			: 'Management_Account_RePassword_refID'
				}, { 
	                fieldLabel	: 'E-mail Address',
	                name      	: 'mail',
	                width 		: '95%',
	                vtype       : 'email',
	                allowBlank  : false
				}, {
					xtype		: 'checkbox',
					fieldLabel	: 'Enable',
            		name		: 'enable'
				}, {
					name		: 'isEdit',
					hidden		: true,
					ref			: 'Management_Account_isEdit_refID'
				}, {
					xtype       : 'RequireFieldLabel'
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
        ezScrum.ModifyAccountForm.superclass.initComponent.apply(this, arguments);       
    },
    doEvent : function() {
		checkUserSession();//global的做法，不太好 但是目前框架只能允許這麼做
		this.checkID();
    },
	checkID	: function() {
		var form = this.getForm();
		var obj = this;
		var ID = this.getForm().findField('account').getValue();
		
		Ext.Ajax.request({
			url     : 'checkAccountID.do',
			params	: { id: ID },
			scope	: this,
			success : function(response) {
				if(eval(response.responseText)) {
					this.doModify();
				} else {					
					Ext.MessageBox.alert('ID: [ ' + ID + ' ] is already existed.');
				}
			},
			failure : function(response){
				Ext.MessageBox.alert('Failure');
			}		
		});
	},
    doModify : function() {
		Ext.Ajax.request({
        	scope	: this,
			url     : this.url,
			params  : this.getForm().getFieldValues(),
			success : function(response) { this.onModifySuccess(response); }	
		});
	},
	onModifySuccess : function(response) {
		var rs = AccountReader.readRecords(response.responseXML);
		if(rs.success) {
			var record = rs.records[0];
			if(record) {
				this.notifyPanel.notify_AddAccount("true", record);
			}
		}
	},
    reset : function() {
        this.getForm().reset();
    },
    setTheRecord: function(record) {
    	this.getForm().setValues({
    		id			: record.get('ID'),
    		name		: record.get('Name'),
    		mail		: record.get('Mail'),
    		enable		: record.get('Enable')
    	});
    },
    initialAddForm: function() {
    	this.Management_Account_UserID_refID.enable();
    	this.Management_Account_Password_refID.allowBlank = false;
    	this.Management_Account_RePassword_refID.allowBlank = false;
    }
});
Ext.reg('Management_ModifyAccountForm', ezScrum.ModifyAccountForm);

ezScrum.window.ModifyAccountWindow = Ext.extend(ezScrum.layout.Window, {
    title       : ' ',
    width		: 600,
    bodyStyle	: 'padding: 5px',
    initComponent : function() {
        var config = {
            layout : 'form',
            items  : [{ xtype: 'Management_ModifyAccountForm', ref: 'Management_ModifyAccountForm_refID' }]
        }
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.window.ModifyAccountWindow.superclass.initComponent.apply(this, arguments);
    },
    showTheWindow_Add	: function(panel) {
    	// initial form info
    	this.Management_ModifyAccountForm_refID.reset();
    	this.Management_ModifyAccountForm_refID.initialAddForm();
    	this.Management_ModifyAccountForm_refID.getForm().setValues({enable : true});
    	this.Management_ModifyAccountForm_refID.notifyPanel = panel;
    	this.Management_ModifyAccountForm_refID.Management_Account_isEdit_refID.setValue(false);
    	
        // initial window info
        this.setTitle('Add New Account');
        this.show();
    }
});

/*
 * notify method
 * 		1. notifyPanel.notify_AddAccount(success, record)
 * 		2. notifyPanel.notify_EditAccount(success, record)
 * 
 * shared with: 
 * 		1. AccountManagement
 * */
var Account_Modify_Window = new ezScrum.window.ModifyAccountWindow();