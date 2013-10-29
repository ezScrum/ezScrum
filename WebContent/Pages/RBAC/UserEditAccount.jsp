<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all-debug.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CommonFormVTypes.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<link rel="stylesheet" type="text/css" href="css/Message.css"/>

<script type="text/javascript">

	Ext.onReady(function() {
		Ext.QuickTips.init();
	
		var accountStore = new Ext.data.Store({
			fields:[
   			{name : 'Name'},
			{name : 'ID'},
			{name : 'Mail'},
			{name : 'Roles'}	
			],
			reader : accountReader
		});

		// User Info Form
		var contentWidget = new Ext.form.FormPanel({
			id			: 'UserForm',
			labelAlign	: 'right',
			region		: 'center',
			frame		: false,
			labelWidth	: 100,
			border		: false,	
			region		: 'center',
			defaultType	: 'textfield',
			store		: accountStore,
			defaults	: {width: 300},
		   	monitorValid  : true,
			items	: [
					{ 
	                    fieldLabel	: 'User ID',
                        name      	: 'id',
                        id			: 'ID',                        
						readOnly	: true
					},
            		{
                        fieldLabel	: 'User Name',
                        name      	: 'username',                        
                        allowBlank  : false
                    },
					{
						id          : 'pass', 
	                    fieldLabel	: 'Password',
                        name      	: 'passwd',
                        inputType   : 'password',
                        listeners   : {
                        	'change': function(){
                        		if (this.getValue() == "") {
                        			Ext.getCmp('UserWidget').getTopToolbar().get('updateAccountBtn').setDisabled(false);
                        			Ext.getCmp("reenter").allowBlank = true;
                        		} else {
                        			Ext.getCmp('UserWidget').getTopToolbar().get('updateAccountBtn').setDisabled(true);
                        			Ext.getCmp("reenter").allowBlank = false;
                        		}
                        	}
                        }
					}, 
					{
						id          : 'reenter',
	                    fieldLabel	: 'Re-enter',
	                    initialPassField	: 'passwd',
                        name      	: 'reenter',
                        inputType   : 'password',
                        initialPassField: 'pass',                       
                        vtype       : 'pwdvalid',
                        listeners   : {       
                        	'change': function() {
								if (Ext.getCmp("reenter").getValue() == "") {
									Ext.getCmp('UserWidget').getTopToolbar().get('updateAccountBtn').setDisabled(true);
                       				this.allowBlank = true;
								} else {
									Ext.getCmp('UserWidget').getTopToolbar().get('updateAccountBtn').setDisabled(false);
									this.allowBlank = false;
								}
                           	}
                        }
					},
					{ 
	                    fieldLabel	: 'E-mail Address',
                        name      	: 'email',
                        vtype       : 'email',
                        allowBlank  : false                        
					}],
			loadStore: function(response) {
				accountStore.loadData(response.responseXML);
			
				var record = accountStore.getAt(0);
				if(record) {
					this.getForm().reset();
					
					this.getForm().setValues({
						id		: record.get('ID'), 
						username: record.get('Name'), 
						passwd	: '', 
						reenter	: '',
						email	: record.get('Mail')
					});
				}
			},
			update: function(response) {
				var form = this.getForm();
				
				Ext.Ajax.request({
					url	: 'updateAccount.do' ,
					params: form.getValues(),
					success: function(response) {
						if (eval(response.responseText)) {
							Ext.example.msg('Update Account', 'Update Account Success');
						} else {
							Ext.example.msg('Update Account', 'Update Account Failure');
						}
					}
				});
			}
		});
		
		Ext.Ajax.request({
			url		: 'getUserData.do',
			success	: function(response) {
				contentWidget.loadStore(response);
			},
			failure	: function() {
				Ext.MessageBox.alert('Sorry, get account information fail.');
			}
		});
		
		var masterWidget = new Ext.Panel({
			id			: 'UserWidget',
			region		: 'center',
			title		: 'User Information',
			renderTo	: Ext.get("content"),
			items		: [contentWidget],
			tbar: [
				{
					id		: 'updateAccountBtn', 
					text	: 'Save Information', 
					icon	: 'images/save.png',
					disabled: false,
					handler	: function() {
						contentWidget.update();
					}			
				},
				{
					id		: 'backBtn',
					text	: 'Back',
					icon	: 'images/back_16.gif',
					handler	: function() {
						document.location.href  = "./viewList.do";
					}
				}		
			]
		});
	});
</script>

<div id="content"></div>