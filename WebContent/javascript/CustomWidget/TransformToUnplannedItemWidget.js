Ext.ns('ezScrum');

var ImplicationIssueField = new Ext.form.NumberField({
	xtype: 'numberfield',
	fieldLabel: 'Implication Issue',
	name: 'implicationID',
	maxLength: 20
});
 
var handlerStore = new Ext.data.Store({
   	fields:[{name : 'Name'}],
	reader : handlerReader
});

var sprintComboStore = new Ext.data.Store({
    fields: [
        {name: 'Id', type: 'int'},
   		{name: 'Name'}
    ]
});
/* Transform  Unplanned Item Form */
ezScrum.TransformToUnplannedItemForm = Ext.extend(Ext.form.FormPanel, {
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
			url : 'ajaxTransformToUnplannedItem.do',
			items: [
				{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 256
		        },
				//由於Implication Issue與Scrum流程暫時沒有想法，所以先mark掉
		        //ImplicationIssueField,
		        {
		        	fieldLabel: 'Sprint',
		        	name:'SprintID',
		        	id:'SprintIDCombo',
		        	xtype:'combo',
		            editable:false, 
		            disabled: true,
		            store: sprintComboStore,
					triggerAction:'all',
					forceSelection: true,
					mode:'local',
	    			valueField: 'Id',
	    			displayField: 'Name'
		        },
		        {
		        	fieldLabel: 'Handler',
		            name: 'Handler',
		            xtype:'combo',
		            editable:false, 
					triggerAction:'all',
					forceSelection: true,
					mode:'local',
					store: handlerStore,
				    valueField: 'Name',
				    displayField: 'Name',
				    listeners: {
	    				// hide cursor
					    'expand': function(combo) {
					        var blurField = function(el) {
					            el.blur();
					        }
					        blurField.defer(10,this,[combo.el]);
					    },
					    'collapse': function(combo) {
					        var blurField = function(el) {
					            el.blur();
					        }
					        blurField.defer(10,this,[combo.el]);
				    	}
					}
		        },
		        {
		        	fieldLabel: 'Partners',
		            name: 'Partners'
		        },
		        {
		        	fieldLabel: 'Estimate',
		            name: 'Estimate'
		        }, 
		        {
		        	fieldLabel: 'Notes',
		            xtype: 'textarea',
		            name: 'Notes',
		            height:200
		        }, 
		        {
		        	fieldLabel: 'Specific Time',
		            name: 'SpecificTime',
		            xtype: 'datefield',
		            format:'Y/m/d',
		            editable: false
		        }, {
		            name: 'm_transFormByID',
		            hidden: true
		        }, {
		            name: 'm_sprintID',
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
		ezScrum.TransformToUnplannedItemForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('TransformSuccess', 'ImplicationFailure', 'TransformFailure');
	},
	onRender:function() {
		ezScrum.TransformToUnplannedItemForm.superclass.onRender.apply(this, arguments);
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

Ext.reg('transformToUnplannedItemForm', ezScrum.TransformToUnplannedItemForm);

ezScrum.TransformToUnplannedItemWidget = Ext.extend(Ext.Window, {
	title:'Transform - Create a Unplanned Item',
	id:'trans2',
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'transformToUnplannedItemForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.TransformToUnplannedItemWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('TransformSuccess', 'ImplicationFailure', 'TransformFailure');
		
		this.items.get(0).on('TransformSuccess', function(obj, response, record){ this.fireEvent('TransformSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('ImplicationFailure', function(obj, response){ this.fireEvent('ImplicationFailure', this, obj, response); }, this);
		this.items.get(0).on('TransformFailure', function(obj, response){ this.fireEvent('TransformFailure', this, obj, response); }, this);
	},
	showWidget:function(transFormById, m_sprintID){
		//set SprintID
		var sprintIDCombo = this.items.get(0).findById('SprintIDCombo');
		sprintIDCombo.originalValue = m_sprintID;
		//reset data
		this.items.get(0).reset();
		//設定sprint ID 讓action取得資訊
		this.items.get(0).getForm().setValues({m_transFormByID: transFormById, m_sprintID: m_sprintID});
		this.show();
	}
});

Ext.Ajax.request({
	url:'AjaxGetHandlerList.do',
	success:function(response){
		handlerStore.loadData(response.responseXML);
	},
	failure:function(){
		alert('Failure');
	}
});