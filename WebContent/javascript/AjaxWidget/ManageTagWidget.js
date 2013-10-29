var tagStore = new Ext.data.Store({
	fields:[{name:'Id', type:'int'},{name:'Name'}],
	reader:tagReader,
	url:'AjaxGetTagList.do',
	autoLoad:true
});

ezScrum.AddTagForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 50,
	defaults: {
        width: 150,
        msgTarget: 'side'
    },
    monitorValid: true,
	initComponent:function() {
		var config = {
			url : 'AjaxAddNewTag.do',
			items: [{
		            fieldLabel: 'Name',
		            name: 'newTagName',
		            allowBlank: false,	
					maxLength: 100		
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
		ezScrum.AddTagForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('AddSuccess', 'AddFailure');
	},
	onRender:function() {
		ezScrum.EditStoryForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var obj = this;
		var form = this.getForm();
		
		Ext.Ajax.request({
			url:this.url,
			success:function(response){
				obj.onAddSuccess(response);				
			},
			failure:function(response){obj.onAddFailure(response);},
			params:form.getValues()
		});
	},
	onAddSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();

		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var q = Ext.DomQuery,
			doc = response.responseXML;
			var rs = tagReader.readRecords(response.responseXML);
			if(rs.success && rs.records.length > 0)
			{
				var record = rs.records[0];
				this.fireEvent('AddSuccess', this, response, record);
			}
			else
			{
				this.fireEvent('AddFailure', this, response, q.selectValue('Message', doc, 'Unknow Error'));
			}
		}		
	},
	onAddFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('AddFailure', this, response, 'Unknow Error!!');
	},
	reset:function()
	{
		this.getForm().reset();
	}

});

Ext.reg('addTagForm', ezScrum.AddTagForm);

ezScrum.AddTagWidget = Ext.extend(Ext.Window, {
	title:'Add Tag',
	width:280,
	modal:false,
	resizable:false,
	closeAction:'hide',
	constrain  : true,
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'addTagForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddTagWidget.superclass.initComponent.apply(this, arguments);
		
		this.addTagForm = this.items.get(0); 
		this.addTagForm.on('AddSuccess', function(obj, response, record){ this.fireEvent('AddSuccess', this, obj, response, record); }, this);
		this.addTagForm.on('AddFailure', function(obj, response, message){ this.fireEvent('AddFailure', this, obj, response, message); }, this);
	},
	addTag:function(){
		this.addTagForm.reset();
		this.show();
	}
});

ezScrum.EditTagForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 50,
	defaults: {
        width: 150,
        msgTarget: 'side'
    },
    monitorValid: true,
	initComponent:function() {
		var config = {
			url : 'AjaxUpdateTag.do',
			items: [{
		            fieldLabel: 'Name',
		            name: 'tagName',
		            allowBlank: false,	
					maxLength: 256		
		        },
		        {
		            name: 'tagId',
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
		ezScrum.EditTagForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('EditSuccess', 'EditFailure');
	},
	onRender:function() {
		ezScrum.EditStoryForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
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
	onEditSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();

		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var q = Ext.DomQuery,
			doc = response.responseXML;
			var rs = tagReader.readRecords(response.responseXML);
			if(rs.success && rs.records.length > 0)
			{
				var record = rs.records[0];
				this.fireEvent('EditSuccess', this, response, record);
			}
			else
			{
				this.fireEvent('EditFailure', this, response, q.selectValue('Message', doc, 'Unknow Error'));
			}
		}		
	},
	onEditFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('EditFailure', this, response, 'Unknow Error!!');
	},
	setTag:function(tagId, tagName)
	{
		this.getForm().setValues({tagId:tagId, tagName: tagName});
	}

});

Ext.reg('editTagForm', ezScrum.EditTagForm);

ezScrum.EditTagWidget = Ext.extend(Ext.Window, {
	title:'Edit Tag',
	width:280,
	modal:false,
	resizable:false,
	closeAction:'hide',
	constrain  : true,
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'editTagForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditTagWidget.superclass.initComponent.apply(this, arguments);
		
		this.editTagForm = this.items.get(0); 
		this.editTagForm.on('EditSuccess', function(obj, response, record){ this.fireEvent('EditSuccess', this, obj, response, record); }, this);
		this.editTagForm.on('EditFailure', function(obj, response, message){ this.fireEvent('EditFailure', this, obj, response, message); }, this);
	},
	editTag:function(tagId, tagName){
		this.editTagForm.setTag(tagId, tagName);
		this.show();
	}
});

var grid = new Ext.grid.GridPanel({
	store: tagStore,
	width: 600,
	region:'center',
	margins: '0 5 5 5',
	autoExpandColumn: 'Name',

	tbar: [{
		icon: 'images/add3.png',
		text: 'Add Tag',
		handler: function(){
			if(!this.addTagWidget)
			{
				this.addTagWidget = new ezScrum.AddTagWidget();
				this.addTagWidget.on('AddSuccess', function(win, obj, response, record){win.hide(); tagStore.add(record);});
				this.addTagWidget.on('AddFailure', function(win, obj, response, message){alert(message);});
			}
			this.addTagWidget.addTag();
		}
	},
	{
		ref: '../removeBtn',
		icon: 'images/delete.png',
		text: 'Remove Tag',
		disabled: true,
		handler: function(){
			// Delete Tag
			var s = grid.getSelectionModel().getSelections();
			for(var i = 0, r; r = s[i]; i++){
				Ext.Ajax.request({
					url:'AjaxDeleteTag.do',
					params:{tagId:r.data['Id']},
					success:function(response){
						var rs = tagReader.readRecords(response.responseXML);
						var id = rs.records[0].data['Id'];
						grid.onDeleteTagSuccess(id);
					}
				});
			}
		}
	}],
	columns: [
		{id:'Name', header:'Tag', dataIndex:'Name', sortable:true}
	],
	onDeleteTagSuccess:function(tagId){
		var record = tagStore.getById(tagId);
		tagStore.remove(record);
	},
	onDeleteTagFailure:function(tagId){
		
	},
	listeners:{
		rowdblclick : function(grid, rowIndex, e)
		{
			if(!this.editTagWidget)
			{
				this.editTagWidget = new ezScrum.EditTagWidget();
				this.editTagWidget.on('EditSuccess', function(win, obj, response, record){
					win.hide(); 
					var index = tagStore.indexOfId(record.data['Id']);
					tagStore.removeAt(index);
			 		tagStore.insert(index,record);
				});
				this.editTagWidget.on('EditFailure', function(win, obj, response, message){alert(message);});
			}
			var record = grid.getStore().getAt(rowIndex);
			this.editTagWidget.editTag(record.data['Id'], record.data['Name']);
		} 
}});
  	
  	
grid.getSelectionModel().on('selectionchange', function(sm){
grid.removeBtn.setDisabled(sm.getCount() < 1);});


var TagWin = new Ext.Window({
       title	: 'Tag Manage',
       width	: 600,
       modal	: false,
       height	: 400,
       constrain: true,
       layout	: 'border',
       layoutConfig : {
           columns  : 1
       },
       width	: 600,
       height	: 600,
       items	: [grid],
       buttons	: [{
			text: 'Cancel',
			scope: this,
			handler : function() {
				TagWin.hide();
			}
	   }],
       closeAction:'hide'
   });
