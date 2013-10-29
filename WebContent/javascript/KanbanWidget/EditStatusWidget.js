Ext.ns('ezScrum');

/* Edit Status Form */
ezScrum.EditStatusForm = Ext.extend(Ext.form.FormPanel, {
	// Default issue id
	issueId : '-1',
	typeId : '-1',
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 100,
	defaults: {
        width: 500,
        msgTarget: 'side'
    },
    monitorValid: true,
	initComponent:function() {
		var config = {
			// Ajax edit Status url 
			url : 'ajaxEditStatus.do',
			// Ajax load Status url
			loadUrl : 'getEditStatusInfo.do',
			
			items: [{
		            fieldLabel: 'ID',
		            name: 'issueID',
					readOnly:true
		        }, {
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
		            emptyText: 'Minimum is 1, and Maximum is 99',
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
	    		handler: this.submit
	    	},
	        {
	        	text: 'Cancel',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditStatusForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
	},
	onRender:function() {
		ezScrum.EditStatusForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
	// Edit Status action 
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
			params:form.getValues()
		});
	},
	// Load Status success
	onLoadSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = jsonStatusReader.read(response);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				/* 初始化控制項為 Status 原本的值 */
				this.getForm().setValues({issueID:record.data['Id'], Name : record.data['Name'], Description : record.data['Description'], Limit : record.data['Limit']});
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
	// Update Status success
	onEditSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = jsonStatusReader.read(response);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('EditSuccess', this, response, record);
			}
		}
		else
			this.fireEvent('EditFailure', this, response, this.issueId);
	},
	// Update Status failure
	onEditFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('EditFailure', this, response, this.issueId);
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
			params : {issueID : this.issueId, typeID : this.typeId}
		});
	},
	reset:function()
	{
		this.getForm().reset();
	}

});
Ext.reg('editStatusForm', ezScrum.EditStatusForm);

ezScrum.EditStatusWidget = Ext.extend(Ext.Window, {
	title:'Edit Status',
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'editStatusForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditStatusWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
		
		this.items.get(0).on('LoadSuccess', function(obj, response, record){ this.fireEvent('LoadSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('LoadFailure', function(obj, response, issueId){ this.fireEvent('LoadFailure', this, obj, response, issueId); }, this);
		this.items.get(0).on('EditSuccess', function(obj, response, record){ this.fireEvent('EditSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('EditFailure', function(obj, response, issueId){ this.fireEvent('EditFailure', this, obj, response, issueId); }, this);
	},
	loadEditStatus:function(issueId, typeID){
		this.items.get(0).getForm().setValues({typeID : typeID});
		this.items.get(0).typeId = typeID;
		this.items.get(0).issueId = issueId;
		this.show();
		this.items.get(0).loadStore();
	}
});