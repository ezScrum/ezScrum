var Edit_AccountInformationStore = new Ext.data.Store({
	fields:[
	    {name : 'ID'},
	    {name : 'Account'},
	   	{name : 'Name'},
		{name : 'Mail'},
		{name : 'Roles'},
		{name : 'Enable'},
		{name : 'Password'}
	],
	reader : AccountReader
});

ezScrum.EditAccountInformationForm = Ext.extend(Ext.form.FormPanel, {
	accountID	  : '-1',
	isEdit		  : false,
	notifyPanel	  : undefined,
	
    border        : false,
    monitorValid  : true,
    bodyStyle     : 'padding:15px',
    defaultType   : 'textfield',
    labelAlign    : 'right',
    labelWidth    : 100,
    store		  : Edit_AccountInformationStore,
    defaults      : {
        width     : 350,
        msgTarget : 'side'
    },
    onRender : function() {
    	ezScrum.EditAccountInformationForm.superclass.onRender.apply(this, arguments);
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
					name		: 'id',
					hidden		: true,
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
        ezScrum.EditAccountInformationForm.superclass.initComponent.apply(this, arguments);       
    },
    doEvent : function() {
		checkUserSession();//global的做法，不太好 但是目前框架只能允許這麼做
		this.Management_Account_UserID_refID.enable();	// 特別 enable user ID 是因為 disable 會讓後端 action 取不到資料 
		this.doModify();
    },
    doModify : function() {
		Ext.Ajax.request({
        	scope	: this,
			url     : this.url,
			params  : {
				id		: this.getForm().findField('id').getValue(),
				account	: this.getForm().findField('account').getValue(),
				name	: this.getForm().findField('name').getValue(),
				enable	: this.getForm().findField('enable').getValue(),
				mail	: this.getForm().findField('mail').getValue(),
				isEdit	: this.getForm().findField('isEdit').getValue()
			},
			success : function(response) { this.onModifySuccess(response); }	
		});
	},
	onModifySuccess : function(response) {
		var rs = AccountReader.readRecords(response.responseXML);
		
		if(rs.success) {
			var record = rs.records[0];
			if(record) {
				this.notifyPanel.notify_EditAccountInformation("true", record);
			}
		}
	},
    reset : function() {
        this.getForm().reset();
    },
    setTheRecord: function(record) {
    	this.getForm().setValues({
    		id			: record.get('ID'),
    		account		: record.get('Account'),
    		name		: record.get('Name'),
    		mail		: record.get('Mail'),
    		enable		: record.get('Enable')
    	});
    },
    initialEditForm: function(userID) {
    	this.Management_Account_UserID_refID.disable();
    	this.Management_Account_isEdit_refID.setValue(true);
    	this.accountID = userID;
    }
});
Ext.reg('Management_EditAccountInformationForm', ezScrum.EditAccountInformationForm);

ezScrum.window.EditAccountInformationWindow = Ext.extend(ezScrum.layout.Window, {
    title       : 'Edit Account Information',
    width		: 600,
    bodyStyle	: 'padding: 5px',
    initComponent : function() {
        var config = {
            layout : 'form',
            items  : [{ xtype: 'Management_EditAccountInformationForm', ref: 'Management_EditAccountInformationForm_refID' }]
        }
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.window.EditAccountInformationWindow.superclass.initComponent.apply(this, arguments);
    },
    showTheWindow_EditInformaiton: function(panel, record) {
    	// initial form info
    	var userID = record.data['ID'];
    	this.Management_EditAccountInformationForm_refID.reset();
    	this.Management_EditAccountInformationForm_refID.initialEditForm(userID);
    	this.Management_EditAccountInformationForm_refID.notifyPanel = panel;
    	
    	// initial window info
    	this.Management_EditAccountInformationForm_refID.setTheRecord(record);
    	this.setTitle('Edit Information');
    	this.show();
    }
});

var Account_ModifyInformation_Window = new ezScrum.window.EditAccountInformationWindow();