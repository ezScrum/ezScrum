Ext.ns('ezScrum');

var accountStore = new Ext.data.Store({
	fields:[
   	{name : 'Name'},
	{name : 'ID'},
	{name : 'Mail'},
	{name : 'Roles'},
	{name : 'Enable'},
	{name : 'Password'}
	],
	reader : accountReader
});

// Create Account Issue Form
ezScrum.CreateAccountForm = Ext.extend(Ext.form.FormPanel, {
    bodyStyle     : 'padding:15px',
    border        : false,
    defaultType   : 'textfield',
    labelAlign    : 'right',
    labelWidth    : 100,
    defaults      : {
        width     : 450,
        msgTarget : 'side'
    },
    store		  : accountStore,
    monitorValid  : true,
    initComponent : function() {
        var config = {
            url     : 'createAccount.do',
            items   : 
            		[{ 
	                    fieldLabel	: 'User ID',
                        name      	: 'id',
                        id			: 'ID',                        
                        width 		: '95%',                                         
                        allowBlank  : false                        
					},
            		{
                        fieldLabel	: 'User Name',
                        name      	: 'name',                        
                        width 		: '95%',                       
                        allowBlank  : false
                        
                    },
					{ 
	                    fieldLabel	: 'Password',
                        name      	: 'passwd',
                        width 		: '95%',
                        inputType   : 'password',
                        id          : 'pass',                       
                        allowBlank  : false
					}, 
					{ 
	                    fieldLabel	: 'Re-enter',
                        name      	: 'reenter',
                        width 		: '95%',
                        inputType   : 'password',
                        allowBlank  : false,
                        initialPassField: 'pass',                       
                        vtype       : 'password'             
					},
					{ 
	                    fieldLabel	: 'E-mail Address',
                        name      	: 'mail',
                        width 		: '95%',
                        vtype       : 'email',
                        allowBlank  : false                        
					}],
			buttons : 
            		[{
						formBind : true,
                        text     : 'Save',
                        scope    : this,
                        handler  : this.checkID
                                                 
                    }, {
                        text    : 'Cancel',
                        scope   : this,
                        handler : function() {this.ownerCt.hide();  }
                    }]
        }
   		Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.CreateAccountForm.superclass.initComponent.apply(this, arguments);       
        
        this.addEvents('CreateSuccess', 'CreateFailure'); 
    },
    onRender:function() {
		ezScrum.CreateAccountForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
    save    : function(){
    	var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();
		
		var form = this.getForm();
        var obj = this;
        
        Ext.Ajax.request({
			url     : obj.url,
			params  : form.getValues(),
			success : function(response) {obj.onCreateSuccess(response);},
			failure : function(response) {obj.onCreateFailure(response);}	
		});
	},
	checkID: function(){
		var form = this.getForm();
        var obj = this;
        var ID = this.getForm().findField('id').getValue();
        
		Ext.Ajax.request({
			url     : 'checkAccountID.do',
			params	: {id: ID},			
			success : function(response)
			{
				if(eval(response.responseText)){
					obj.save();					
				}else{					
					Ext.MessageBox.alert('Invalid ID!! >"<', 'Sorry, ID is invalid.');
				}
			},
			failure : function(response){
				Ext.MessageBox.alert('Failure');
			}		
		})
	},
	onCreateSuccess:function(response){		
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
				
		var rs = accountReader.readRecords(response.responseXML);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('CreateSuccess', this, response, record);
			}
		}
		else
			this.fireEvent('CreateFailure', this, response);
	},
	onCreateFailure:function(response){
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('CreateFailure', this, response);		
	},
    reset: function() {
        this.getForm().reset();
    }				
});
Ext.reg('CreateAccountForm', ezScrum.CreateAccountForm);

ezScrum.CreateAccountWidget = Ext.extend(Ext.Window, {
	title	: 'Create Account',
	width	: 600,
	modal	: true,
	constrain : true,
	closeAction	: 'hide',
	initComponent : function() {
		var config = {
			layout : 'form',
			items  : [{	xtype : 'CreateAccountForm'	}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		/* QuickTips */
		Ext.QuickTips.init();
		ezScrum.CreateAccountWidget.superclass.initComponent.apply(this, arguments);
		

		this.addEvents('CreateSuccess', 'CreateFailure');
		this.items.get(0).on('CreateSuccess', function(obj, response, record) { this.fireEvent('CreateSuccess', this, response, record); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response) { this.fireEvent('CreateFailure', this, response); }, this);
		
	},
	showWidget : function() {	
		this.items.get(0).reset();
		this.show();
	}

});
