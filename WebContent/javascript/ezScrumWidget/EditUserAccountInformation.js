var UserInformation_AccountStore = new Ext.data.Store({
	fields:[
		{name : 'Name'},
		{name : 'ID'},
		{name : 'Account'},
		{name : 'Mail'},
		{name : 'Enable'},
		{name : 'Roles'}	
	],
	reader : AccountReader
});

ezScrum.EditUserAccountInformationForm = Ext.extend(Ext.form.FormPanel, {
	id			    : 'UserInformation_Management_Form',
    store		    : UserInformation_AccountStore,
    bodyStyle       : 'padding:50px',
    monitorValid 	: true,
	autoHeight		: true,
	buttonAlign		: 'left',
    defaultType     : 'textfield',
    labelAlign      : 'right',
    labelWidth      : 100,
    defaults        : {
    	msgTarget   : 'side',
    },
    initComponent : function() {
        var config = {
        	getUserDataUrl	: 'getUserData.do',
        	modifyUrl	    : 'updateAccount.do' ,
            items   : [{ 
	                fieldLabel	: 'User ID',
	                name      	: 'account',
	                width 		: '200',                                         
	                ref			: 'Management_UserAccount_UserID_refID',
	                regex : /^[\w-_()~ ]*$/,
	                regexText : 'Only a-z,A-Z,0-9,-,_,(,),~,space allowed.'
				}, {
	                fieldLabel	: 'User Name',
	                name      	: 'name',                        
	                width 		: '200',                       
	                allowBlank  : false,
	        		regex : /^[^"'\\><&]*$/,
	        		regexText : 'deny following char " < > \\ & \''
				}, { 
	                fieldLabel	: 'E-mail Address',
	                name      	: 'mail',
	                width 		: '200',
	                vtype       : 'email',
	                allowBlank  : false
				}, {
					name		: 'id',
					hidden		: true
				}, {
					xtype       : 'RequireFieldLabel'
				}],
			buttons : [{
					formBind : true,
                    text     : 'Save',
                    scope    : this,
                    handler  : this.doEvent
                }
            ]
        }
   		Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.EditUserAccountInformationForm.superclass.initComponent.apply(this, arguments);       
    },
    doEvent : function() {
		checkUserSession();//global的做法，不太好 但是目前框架只能允許這麼做
		this.doModify();
    },
    
    /**
     * 修改後端資料
     * */
    doModify : function() {
		Ext.Ajax.request({
        	scope	: this,
			url     : this.modifyUrl,
			params  : {
				id		: this.getForm().findField('id').getValue(),
				account	: this.getForm().findField('account').getValue(),
				name	: this.getForm().findField('name').getValue(),
				enable	: this.getRecord().data['Enable'],
				mail	: this.getForm().findField('mail').getValue()
			},
			success : function(response) { 
				var rs = AccountReader.readRecords(response.responseXML);
				var title = "Edit Information";
				if(rs.success) {
					this.doModifyTopPanelUserName();
					Ext.example.msg(title, "Success.");
				}else{
					Ext.example.msg(title, "Sorry, please try again.");
				}
			}	
		});
	},
	/**
	 * 修改前端Top Panel上User的資料
	 */
	doModifyTopPanelUserName : function() {
		var obj = this;
		Ext.Ajax.request({
			url: 'GetTopTitleInfo.do',
			success: function(response) {
				var obj = Ext.util.JSON.decode(response.responseText);
				var userName = obj.UserName;
				
				Ext.getDom("UserNameInfo_Management").innerHTML = userName;
			},
			failure : function(){
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
		});
	},
    setRecord: function(record) {
    	this.getForm().reset();
    	this.getForm().findField('account').disable();
    	this.getForm().setValues({
    		id			: record.get('ID'),
    		account		: record.get('Account'),
    		name		: record.get('Name'),
    		enable  	: record.get('Enable'),
    		mail		: record.get('Mail')
    	});
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
	}
});
Ext.reg('Management_EditUserAccountInformationForm', ezScrum.EditUserAccountInformationForm);