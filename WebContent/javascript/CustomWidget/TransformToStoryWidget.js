Ext.ns('ezScrum');

/* Create Story Form */
ezScrum.TransformToStoryForm = Ext.extend(Ext.form.FormPanel, {
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
			url : 'ajaxTransformToStory.do',
			items: [{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 128
		        },
		        /*
		        {
					xtype: 'numberfield',
					fieldLabel: 'Implication Issue',
					name: 'implicationID',
					maxLength: 20
				},*/
		        {
		            fieldLabel: 'Value',
		            name: 'Value',
		            vtype:'Number'
		        }, {
		            fieldLabel: 'Estimate',
		            name: 'Estimate',
		            vtype:'Float'
		        }, {
		            fieldLabel: 'Importance',
		            name: 'Importance',
		            vtype:'Number'
		        },  {
		        	fieldLabel: 'Notes',
		            xtype: 'textarea',
		            name: 'Notes',
		            height:200
		        }, {
		        	fieldLabel: 'How To Demo',
		            xtype: 'textarea',
		            name: 'HowToDemo',
		            height:200
		        }, {
		            name: 'transFormByID',
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
		ezScrum.TransformToStoryForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('TransformSuccess', 'ImplicationFailure', 'TransformFailure');
	},
	onRender:function() {
		ezScrum.TransformToStoryForm.superclass.onRender.apply(this, arguments);
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
		if(response.responseText == "false"){
			myMask.hide();
			this.fireEvent('ImplicationFailure', this, response);
		} 
		else{
			myMask.hide();
			var rs = jsonCustomIssueReader.read(response);
			if(rs.success){
				var record = rs.records[0];
				if(record){
					this.fireEvent('TransformSuccess', this, response, record);
				}
			}
			else
				this.fireEvent('TransformFailure', this, response);
		}
	},
	onFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('TransformFailure', this, response);
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('transformToStoryForm', ezScrum.TransformToStoryForm);

ezScrum.TransformCustomIssueWidget = Ext.extend(Ext.Window, {
	title:'Transform - Create a Story',
	id:'trans',
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'transformToStoryForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.TransformCustomIssueWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('TransformSuccess', 'ImplicationFailure', 'TransformFailure');
		
		this.items.get(0).on('TransformSuccess', function(obj, response, record){ this.fireEvent('TransformSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('ImplicationFailure', function(obj, response){ this.fireEvent('ImplicationFailure', this, obj, response); }, this);
		this.items.get(0).on('TransformFailure', function(obj, response){ this.fireEvent('TransformFailure', this, obj, response); }, this);
	},
	showWidget:function(transFormById){
		this.items.get(0).reset();
		this.items.get(0).getForm().setValues({transFormByID : transFormById});
		this.show();
	}
});
