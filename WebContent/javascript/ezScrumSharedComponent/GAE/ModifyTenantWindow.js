var Modify_TenantStore = new Ext.data.Store({
	fields:[
	   	{name : 'ID'},	// alex
		{name : 'Name'},
//		{name : 'Mail'},
		{name : 'Enable'},
//		{name : 'Period'},
//		{name : 'ActivateDate'},
		{name : 'Description'},
		{name : 'AdminName'}
		
//		,{name : 'Password'}
	],
	reader : TenantReader
});

ezScrum.ModifyTenantForm = Ext.extend(Ext.form.FormPanel, {
	tenantID	  : '-1',
	isEdit		  : false,
	notifyPanel	  : undefined,
	
    border        : false,
    monitorValid  : true,
    bodyStyle     : 'padding:15px',
    defaultType   : 'textfield',
    labelAlign    : 'right',
    labelWidth    : 100,
    store		  : Modify_TenantStore,
    defaults      : {
        width     : 350,
        msgTarget : 'side'
    },
    onRender : function() {
    	ezScrum.ModifyTenantForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
    initComponent : function() {
        var config = {
            url     : 'modifyTenant.do',
            loadUrl : 'showTenantInfo.do',
            items   : [{ 
	                fieldLabel	: 'Tenant ID',
	                name      	: 'id',
	                width 		: '95%',                                         
	                allowBlank  : false,
	                ref			: 'Management_Tenant_TenantID_refID',
	                regex : /^[\w-_()~ ]*$/,
	                regexText : 'Only a-z,A-Z,0-9,-,_,(,),~,space allowed.'
				}, {
	                fieldLabel	: 'Tenant Name',
	                name      	: 'name',                        
	                width 		: '95%',                       
	                allowBlank  : false,
	        		regex : /^[^"'\\><&]*$/,
	        		regexText : 'deny following char " < > \\ & \''
	            }
				, {
	                fieldLabel	: 'Description',
	                name      	: 'description',                        
	                width 		: '95%',
	                height		: 150,
	                xtype		: 'textarea',
	                allowBlank  : false
	            }
				
				
//				, { 
//	                fieldLabel	: 'Password',
//	                name      	: 'passwd',
//	                width 		: '95%',
//	                inputType   : 'password',
//	                id          : 'Modify_Tenant_Form_Password',
//	                allowBlank  : false,hidden		: true,
//	                ref			: 'Management_Tenant_Password_refID'
//				}, { 
//	                fieldLabel	: 'Re-enter',
//	                name      	: 'reenter',
//	                width 		: '95%',
//	                inputType   : 'password',
//	                allowBlank  : false,hidden		: true,
//	                initialPassField: 'Modify_Tenant_Form_Password',                       
//	                vtype       : 'password',
//	                ref			: 'Management_Tenant_RePassword_refID'
//				}
//				, { 
//	                fieldLabel	: 'E-mail Address',
//	                name      	: 'mail',
//	                width 		: '95%',
//	                vtype       : 'email',
//	                allowBlank  : false
//				}
				, {
					name		: 'separator',
					xtype		: 'menuseparator'
				}
				, {
					name		: 'isEdit',
					hidden		: true,
					ref			: 'Management_Tenant_isEdit_refID'
				},{ 
	                fieldLabel	: 'Admin Name',
	                name      	: 'adminname',
	                width 		: '95%',                                         
	                allowBlank  : false,
	                ref			: 'Management_Tenant_AdminID_refID',
	                regex : /^[\w-_()~ ]*$/,
	                regexText : 'Only a-z,A-Z,0-9,-,_,(,),~,space allowed.'
				},
//				{
//					// Activative Date
//					fieldLabel : 'Activative Date',
//					name       : 'activativedate',
//					xtype	   : 'datefield',
//					allowBlank : false,
//			        format     : 'Y/m/d',
//			        altFormats : 'Y/m/d',
//			        anchor     : '95%'
//				},{ 
//	                fieldLabel	: 'Period(Days)',
//	                name      	: 'period',
//	                width 		: '95%',                                         
//	                allowBlank  : false,
//	                regex : /^[0-9]*$/,
//	                regexText : 'Only 0-9 allowed.'
//				},
				{
					xtype		: 'checkbox',
					fieldLabel	: 'Enable',
            		name		: 'enable'
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
        ezScrum.ModifyTenantForm.superclass.initComponent.apply(this, arguments);       
    },
    doEvent : function() {
		checkUserSession();//global的做法，不太好 但是目前框架只能允許這麼做
    	if (this.isEdit) {
    		this.Management_Tenant_TenantID_refID.enable();	// 特別 enable user ID 是因為 disable 會讓後端 action 取不到資料 
    		this.Management_Tenant_AdminID_refID.enable();
    		this.doModify();
    	} else {
    		this.checkID();
    	}
    },
	checkID	: function() {
		var form = this.getForm();
		var obj = this;
		var ID = this.getForm().findField('id').getValue();
		
		Ext.Ajax.request({
			url     : 'checkTenantID.do',
			params	: { id: ID },
			scope	: this,
			success : function(response) {
				if(eval(response.responseText)) {
					this.doModify();
				} else {					
					Ext.MessageBox.alert('ID: [ ' + ID + ' ] is is already existed.');
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
		var rs = TenantReader.readRecords(response.responseXML);
		if(rs.success) {
			var record = rs.records[0];
			if(record) {
				if (this.isEdit) {
					this.notifyPanel.notify_EditTenant("true", record);
				} else {
					this.notifyPanel.notify_AddTenant("true", record);
				}
			}
		}
	},
	
    loadDataModel	: function(id) {
    	var obj = this;
        var form = this.getForm();
    	Ext.Ajax.request({
			url		: obj.loadUrl,
			params	: { id : id },
			success	: function(response) { obj.onLoadSuccess(response); },
			failure : function(response) { /* notify logon form, not finish yet */ }
		});
    },
	
    onLoadSuccess: function(response) {
    	var rs = TenantReader.readRecords(response.responseXML);
		if(rs.success) {
			var record = rs.records[0];
	    	this.getForm().setValues({
	    		id			: record.get('ID'),
	    		name		: record.get('Name'),
//	    		mail		: record.get('Mail'),
	    		adminname		: record.get('AdminName'),
	    		description		: record.get('Description'),
//	    		activativedate		: record.get('ActivativeDate'),
	    		enable		: record.get('Enable')
	    	});
		}
    },
    reset : function() {
        this.getForm().reset();
        this.getForm().setValues({enable : true});
    },
    
    initialAddForm: function() {
    	this.reset();
    	this.Management_Tenant_TenantID_refID.enable();
    	this.Management_Tenant_AdminID_refID.enable();
//    	this.Management_Tenant_Password_refID.allowBlank = false;
//    	this.Management_Tenant_RePassword_refID.allowBlank = false;
    },
    initialEditForm: function() {
    	this.reset();
    	this.Management_Tenant_TenantID_refID.disable();
    	this.Management_Tenant_AdminID_refID.disable();
//    	this.Management_Tenant_Password_refID.allowBlank = true;
//    	this.Management_Tenant_RePassword_refID.allowBlank = true;
    }
});
Ext.reg('Management_ModifyTenantForm', ezScrum.ModifyTenantForm);

ezScrum.window.ModifyTenantWindow = Ext.extend(ezScrum.layout.Window, {
    title       : ' ',
    width		: 600,
    bodyStyle	: 'padding: 5px',
    initComponent : function() {
        var config = {
            layout : 'form',
            items  : [{ xtype: 'Management_ModifyTenantForm', ref: 'Management_ModifyTenantForm_refID' }]
        }
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.window.ModifyTenantWindow.superclass.initComponent.apply(this, arguments);
    },
    showTheWindow_Add	: function(panel) {
    	// initial form info
    	this.Management_ModifyTenantForm_refID.initialAddForm();
    	this.Management_ModifyTenantForm_refID.isEdit = false;
    	this.Management_ModifyTenantForm_refID.Management_Tenant_isEdit_refID.setValue(false);
    	this.Management_ModifyTenantForm_refID.notifyPanel = panel;
    	
        // initial window info
        this.setTitle('Add New Tenant With Rent Service');
        this.show();
    },
    showTheWindow_Edit: function(panel, id) {
    	// initial form info
    	this.Management_ModifyTenantForm_refID.initialEditForm();
    	this.Management_ModifyTenantForm_refID.notifyPanel = panel;
    	this.Management_ModifyTenantForm_refID.isEdit = true;
    	this.Management_ModifyTenantForm_refID.Management_Tenant_isEdit_refID.setValue(true);
    	this.Management_ModifyTenantForm_refID.tenantID = id;
    	this.Management_ModifyTenantForm_refID.loadDataModel(id);
    	
        // initial window info
    	this.setTitle('Edit Tenant # ' + id);
    	this.show();
    }
});

/*
 * call method
 * 		1. showTheWindow_Add: function(panel)
 * 		2. showTheWindow_Edit: function(panel, tenantID)
 * 
 * notify method
 * 		1. notifyPanel.notify_AddTenant(success, record)
 * 		2. notifyPanel.notify_EditTenant(success, record)
 * 
 * shared with: 
 * 		1. TenantManagement
 * */
var Tenant_Modify_Window = new ezScrum.window.ModifyTenantWindow();