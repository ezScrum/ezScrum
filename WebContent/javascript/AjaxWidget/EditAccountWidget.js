Ext.ns('ezScrum');

// Create Account Issue Form
ezScrum.EditAccountForm = Ext.extend(Ext.form.FormPanel, {
	id   		  : '-1',
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
            url     : 'updateAccount.do',
            loadUrl : 'showAccountInfo.do',
            items   : 
            		[{ 
	                    fieldLabel	: 'User ID',
                        name      	: 'id',                       
                        width 		: '95%',                                         
                        readOnly    : true                        
					},
            		{
                        fieldLabel	: 'User Name',
                        name      	: 'username',                        
                        width 		: '95%',                       
                        allowBlank  : false
                        
                    },
					{ 
	                    fieldLabel	: 'Password',
	                    inputType   : 'password',
                        name      	: 'passwd',
                        id          : 'passwd',
                        width 		: '95%',
                        listeners   : {
                        	'change': function(){
                        		if (this.getValue() == "") {
                        			Ext.getCmp("reenter").allowBlank = true;
                        		} else {
                        			Ext.getCmp("reenter").allowBlank = false;
                        		}
                        	}
                        }
					}, 
					{ 
	                    fieldLabel	: 'Re-enter',
	                    inputType   : 'password',
                        name      	: 'reenter',
                        id          : 'reenter',
                        width 		: '95%',
                        initialPassField: 'passwd',                       
                        vtype       : 'password'
					},
					{ 
	                    fieldLabel	: 'E-mail Address',
                        name      	: 'email',
                        width 		: '95%',
                        vtype       : 'email',
                        allowBlank  : false                        
					},
					{
						xtype: 'checkbox',
						fieldLabel: 'Enable',
                		name: 'enable'
					}],
			buttons : 
            		[{
						formBind : true,
                        text     : 'Update',
                        scope    : this,
                        handler  : this.submit
                                                 
                    }, {
                        text    : 'Cancel',
                        scope   : this,
                        handler : function() {this.ownerCt.hide();  }
                    }]
        }
   		Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.EditAccountForm.superclass.initComponent.apply(this, arguments);       
        
        this.addEvents('CreateSuccess', 'CreateFailure'); 
    },
    onRender:function() {
		ezScrum.EditAccountForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
	// Load Account success
	onLoadSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = accountReader.readRecords(response.responseXML);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
        		Ext.getCmp("reenter").allowBlank = true;
				this.getForm().setValues({id : record.data['ID'], username : record.data['Name'], email : record.data['Mail'], enable : record.data['Enable']});
				this.fireEvent('LoadSuccess', this, response, record);
			}
		}
		
	},
	onLoadFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('LoadFailure', this, response, this.issueId);
	},
	// Update Unplanned Item success
	onEditSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = accountReader.readRecords(response.responseXML);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('EditSuccess', this, response, record);
			}
		}
		else
			this.fireEvent('EditFailure', this, response, this.id);
	},
	// Update Unplanned Item failure
	onEditFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('EditFailure', this, response, this.id);
		
	},
	loadStore:function()
	{
		var obj = this;
		var myMask = new Ext.LoadMask(obj.getEl(), {msg:"Please wait..."});
		myMask.show();
		Ext.Ajax.request({
			url:this.loadUrl,
			success:function(response){obj.onLoadSuccess(response);},
			failure:function(response){obj.onLoadFailure(response);},
			params : {id : this.id}
		});
	},
	reset:function()
	{
		this.getForm().reset();
	},
    // Edit Unplanned Item action 
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var obj = this;
		var form = this.getForm();

		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onEditSuccess(response);},
			failure:function(response){obj.onEditFailure(response);},
			params:form.getFieldValues()
		});
		
	}					
});

Ext.reg('EditAccountForm', ezScrum.EditAccountForm);

ezScrum.EditAccountWidget = Ext.extend(Ext.Window, {
	title	: 'Edit Account',
	width	: 600,
	modal	: true,
	constrain : true,
	closeAction	: 'hide',
	initComponent : function() {
		var config = {
			layout : 'form',
			items  : [{	xtype : 'EditAccountForm'	}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		/* QuickTips */
		Ext.QuickTips.init();
		ezScrum.EditAccountWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
		this.items.get(0).on('LoadSuccess', function(obj, response, record){ this.fireEvent('LoadSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('LoadFailure', function(obj, response, issueId){ this.fireEvent('LoadFailure', this, obj, response, id); }, this);
		this.items.get(0).on('EditSuccess', function(obj, response, record){ this.fireEvent('EditSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('EditFailure', function(obj, response, issueId){ this.fireEvent('EditFailure', this, obj, response, id); }, this);		
	},
	loadEditAccount:function(id){
		this.items.get(0).id = id;
		this.items.get(0).reset();
		this.show();
		this.items.get(0).loadStore();
	}
});