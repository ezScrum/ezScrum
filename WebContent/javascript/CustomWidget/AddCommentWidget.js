Ext.ns('ezScrum');

/* Add Comment Form */
ezScrum.AddCommentForm = Ext.extend(Ext.form.FormPanel, {
	issueId:'-1',
	projectName: '',
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
			url : 'addCommentByTeam.do',
			items: [{
		        	fieldLabel: 'Comment',
		            xtype: 'textarea',
		            name: 'Comment',
		            disabled: true,
		            height:200
				}, {
		        	fieldLabel: 'Add Comment',
		            xtype: 'textarea',
		            name: 'AddComment',
		            height:200,
		            allowBlank: false
		        }, {
		            name: 'typeID',
		            hidden: true
		        }, {
			        name: 'OldComment',
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
		ezScrum.AddCommentForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('AddSuccess', 'AddFailure');
	},
	loadStore:function(){
		var obj = this;
		var myMask = new Ext.LoadMask(obj.getEl(), {msg:"Please wait..."});
		myMask.show();
		Ext.Ajax.request({
			url:'getCommentInfo.do',
			success:function(response){obj.onLoadSuccess(response);},
			failure:function(response){obj.onLoadFailure(response);},
			params : {issueID : this.issueId, projectName : this.projectName}
		});
	},
	// Load Custom Issue success
	onLoadSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = jsonCustomIssueReader.read(response);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				this.getForm().setValues({
				 	Comment : record.data['Comment'],
				 	OldComment : record.data['Comment']
				});
			}
		}
	},
	onLoadFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('LoadFailure', this, response, this.issueId);
	},
	onRender:function() {
		ezScrum.AddCommentForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var form = this.getForm();
		var addUrl = this.url + "?issueID=" + this.issueId+"&projectName="+this.projectName;
		var obj = this;
		Ext.Ajax.request({
			url:addUrl,
			success:function(response){obj.onSuccess(response);},
			failure:function(response){obj.onFailure(response);},
			params:form.getValues()
		});
	},
	onSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = jsonCustomIssueReader.read(response);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('AddSuccess', this, response, record);
			}

		}
		else
			this.fireEvent('AddFailure', this, response);
	},
	onFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('AddFailure', this, response);
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('addCommentForm', ezScrum.AddCommentForm);

ezScrum.AddCommentWidget = Ext.extend(Ext.Window, {
	title:'Add Comment',
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'addCommentForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddCommentWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('AddSuccess', 'AddFailure');
		
		this.items.get(0).on('AddSuccess', function(obj, response, record){ this.fireEvent('AddSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('AddFailure', function(obj, response){ this.fireEvent('AddFailure', this, obj, response, issueId); }, this);
	},
	showWidget1:function(issueID){
		this.items.get(0).reset();
		this.items.get(0).issueId = issueID; 
		this.show();
		this.items.get(0).loadStore();
	},
	showWidget:function(issueID, projectName){
		this.items.get(0).reset();
		this.items.get(0).issueId = issueID;
		this.items.get(0).projectName = projectName;
		this.show();
		this.items.get(0).loadStore();
	}
});