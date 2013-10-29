Ext.ns('ezScrum');

/* Create Status Form */
ezScrum.CreateStatusForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 100,
	defaults: {
        width: 500,
        msgTarget: 'side'
    },
    monitorValid:true,
	initComponent:function() {	
		var config = {
			url : 'ajaxAddNewStatus.do',
			items: [{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 128
		        }, {
		        	fieldLabel: 'Description',
		            xtype: 'textarea',
		            name: 'Description',
		            height:100
		        }, {
		            fieldLabel: 'Limit',
		            xtype: 'numberfield',
		            name: 'Limit',
		            allowBlank: false,
		            emptyText: 'Minimum is 0, and Maximum is 99',
		            minValue: 0,
		            maxValue: 99,
		            maxLength: 2
		        }, {
		            name: 'typeID',
		            hidden: true
		        }
		    ],
		    buttons: 
		    [{
		    	formBind:true,
	    		text: 'Submit',
	    		scope:this,
	    		handler: this.submit,
	    		disabled:true
	    	},
	        {
	        	text: 'Cancel',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.CreateStatusForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
	},
	onRender:function() {
		ezScrum.CreateStatusForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var form = this.getForm();

		var obj = this;
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onSuccess(response);},
			failure:function(response){obj.onFailure(response);},
			params:form.getValues()
		});
	},
	onSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = jsonStatusReader.read(response);
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
	onFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('CreateFailure', this, response);
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('createStatusForm', ezScrum.CreateStatusForm);

ezScrum.AddNewStatusWidget = Ext.extend(Ext.Window, {
	title:'Add New Status',
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'createStatusForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddNewStatusWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
		
		this.items.get(0).on('CreateSuccess', function(obj, response, record){ this.fireEvent('CreateSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response){ this.fireEvent('CreateFailure', this, obj, response, issueId); }, this);
	},
	showWidget:function(typeID){
		this.items.get(0).reset();
		this.items.get(0).getForm().setValues({typeID : typeID});
		this.show();
	}
});