var UserInformation_AccountStore = new Ext.data.Store({
	fields:[
		{name : 'Name'},
		{name : 'ID'},
		{name : 'Mail'},
		{name : 'Roles'}	
	],
	reader : AccountReader
});

var UserInformationItem = [
	{ 
        fieldLabel	: 'User ID',
        name      	: 'id',
		readOnly	: true
	}, {
        fieldLabel	: 'User Name',
        name      	: 'username',                        
        allowBlank  : false,
		regex : /^[^"'\\><&]*$/,
		regexText : 'deny following char " < > \\ & \''
    }, {
		id          : 'UserInformation_Form_Password', 
        fieldLabel	: 'Password',
        name      	: 'passwd',
        inputType   : 'password',
        listeners   : {
        	'change': function() {
        		if (this.getValue() == "") {
        			Ext.getCmp('UserInformationManagement_Page').getTopToolbar().get('UserInformation_UpdateAccountBtn').setDisabled(false);
        			Ext.getCmp("UserInformation_Form_reenter").allowBlank = true;
        		} else {
        			Ext.getCmp('UserInformationManagement_Page').getTopToolbar().get('UserInformation_UpdateAccountBtn').setDisabled(true);
        			Ext.getCmp("UserInformation_Form_reenter").allowBlank = false;
        		}
        	}
        }
	}, {
		id          : 'UserInformation_Form_reenter',
        fieldLabel	: 'Re-enter',
        initialPassField	: 'passwd',
        name      	: 'reenter',
        inputType   : 'password',
        initialPassField: 'UserInformation_Form_Password',                       
        vtype       : 'pwdvalid',
        listeners   : {       
        	'change': function() {
				if (Ext.getCmp("UserInformation_Form_Password").getValue() == "") {
					Ext.getCmp('UserInformationManagement_Page').getTopToolbar().get('UserInformation_UpdateAccountBtn').setDisabled(true);
       				this.allowBlank = true;
				} else {
					Ext.getCmp('UserInformationManagement_Page').getTopToolbar().get('UserInformation_UpdateAccountBtn').setDisabled(false);
					this.allowBlank = false;
				}
           	}
        }
	}, { 
        fieldLabel	: 'E-mail Address',
        name      	: 'email',
        vtype       : 'email',
        allowBlank  : false
	}
];

// the form is for UserInformation Page
ezScrum.UserInformationForm = Ext.extend(ezScrum.layout.InfoForm, {
	id			: 'UserInformation_Management_From',
	url			: 'getUserData.do',
	modifyurl	: 'updateAccount.do' ,
    store		: UserInformation_AccountStore,
    width:500,
    bodyStyle:'padding:50px',
    monitorValid : true,
	buttonAlign:'center',
	initComponent : function() {
		var config = {
			items	: [UserInformationItem],
			buttons : [{
				formBind : true,
				text : 'Submit',
				scope : this,
				handler : this.submit,
				disabled : true
			}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.UserInformationForm.superclass.initComponent.apply(this, arguments);
	},
	submit : function() {
		Ext.getCmp('UserInformationManagement_Page').doModify();
	},
	loadDataModel: function() {
		UserManagementMainLoadMaskShow();
		Ext.Ajax.request({
			scope	: this,
			url 	: this.url,
			success: function(response) {
				UserInformation_AccountStore.loadData(response.responseXML);
				var record = UserInformation_AccountStore.getAt(0);
    			this.setDataModel(record);
    			
    			UserManagementMainLoadMaskHide();
			},
			failure: function(response) {
				UserManagementMainLoadMaskHide();
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
	setDataModel: function(record) {
		this.getForm().reset();
		this.getForm().setValues({
			id		: record.data['ID'], 
			username: record.data['Name'], 
			passwd	: '', 
			reenter	: '',
			email	: record.data['Mail']
		});
	},
    doModify: function() {
    	UserManagementMainLoadMaskShow();
    	Ext.Ajax.request({
    		scope	: this,
    		url		: this.modifyurl,
    		params	: this.getForm().getValues(),
    		success	: function(response) {
    			UserInformation_AccountStore.loadData(response.responseXML);
				var record = UserInformation_AccountStore.getAt(0);
    			if (record) {
					Ext.example.msg('Update Account', 'Update Account Success');
				} else {
					Ext.example.msg('Update Account', 'Update Account Failure');
				}
    			
    			UserManagementMainLoadMaskHide();
    		},
    		failure:function(response){
    			UserManagementMainLoadMaskHide();
    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    		}
    	});
	}
});
Ext.reg('UserInformationForm', ezScrum.UserInformationForm);