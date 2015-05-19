Ext.ns('ezScrum');

/*-----------------------------------------------------------
 *	取得Tag列表，儲存到Tag Store中
 -------------------------------------------------------------*/
var CreateStoryWidgetTagStore_forEdit = new Ext.data.Store({
    fields   : [{
                name : 'Id',
                type : 'int'
            }, {
                name : 'Name'
            }],
    reader   : tagReader,
    url      : 'AjaxGetTagList.do',
    autoLoad : true
});

/*-----------------------------------------------------------
 *  TagStore更新完畢之後，自動更新TagMenu
 -------------------------------------------------------------*/
CreateStoryWidgetTagStore_forEdit.on('load', function(store, records, options){
    tagMenu_forEdit.removeAll();
    var tagCount = CreateStoryWidgetTagStore_forEdit.getTotalCount();
    for (var i = 0; i < tagCount; i++)
    {
        var tagRecord = CreateStoryWidgetTagStore_forEdit.getAt(i);
        tagMenu_forEdit.add({
                    tagId        : tagRecord.data['Id'],
                    text         : tagRecord.data['Name'],
                    xtype        : 'menucheckitem',
                    hideOnClick  : false,
                    checkHandler : tagMenu_forEdit.onCheckItemClick
                });
    }
    //確保 setcheck 在 TagMenu 更新後才做
    tagMenu_forEdit.setInitTagInfo();
});

var tagMenu_forEdit = new Ext.menu.Menu({
    /*-----------------------------------------------------------
     *   當CheckItem被點選之後，更新TagTriggerField上的文字
    -------------------------------------------------------------*/
    storyRecord : undefined,
        
    onCheckItemClick : function(item, checked)
    {
        var tagRaw = tagTriggerField_forEdit.getValue();
        var tagIDRaw = tagIDTextField_forEdit.getValue();
        
        if (tagRaw.length != 0) {
            tags = tagRaw.split(",");
            tagIDs = tagIDRaw.split(",");
        } else {
            tags = [];
            tagIDs = [];
        }
        if (checked) {
            tags.push(item.text);

            tagIDs.push(item.tagId);

        } else {
            var index = tags.indexOf(item.text);
            tags.splice(index, 1);
            tagIDs.splice(index, 1);
        }
        tagTriggerField_forEdit.setValue(tags.join(","));
        tagIDTextField_forEdit.setValue(tagIDs.join(","));
    },
    
    setStoryRecord : function(Record){
    	this.storyRecord = Record;
    },
    
	//setChecke the original tags of story
	setInitTagInfo : function() {
		if(this.storyRecord !== undefined) {
			this.setStoryRecord(this.storyRecord);
			var tags = this.storyRecord.data['Tag'].split(',');
			this.items.each(function(){
				for(var i = 0; i < tags.length; i++){
					if(this.text == tags[i])
						this.setChecked(true);
				}
			});
		}
	}
});

var tagTriggerField_forEdit = new Ext.form.TriggerField({
    fieldLabel : 'Tags',
    name       : 'Tags',
    editable   : false
});
var tagIDTextField_forEdit = new Ext.form.TextField({
    xtype  : 'textarea',
    name   : 'TagIDs',
    hidden : true
});

tagTriggerField_forEdit.onTriggerClick = function(){
    tagMenu_forEdit.showAt(tagTriggerField_forEdit.getPosition());
};

/* Edit Story Form */
ezScrum.EditStoryForm = Ext.extend(Ext.form.FormPanel, {
	// Default issue id
	issueId : '-1',
	storyId: '-1',
	record: undefined,
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
			// Ajax edit story url 
			url : 'ajaxEditStory.do',
			// Ajax load story url
			loadUrl : 'getEditStoryInfo.do',
			
			items: [{
		            fieldLabel: 'ID',
		            name: 'issueID',
					readOnly:true
		        },{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,	
					maxLength: 128
		        },{
		            fieldLabel: 'Value',
		            name: 'Value',
		            vtype: 'Number'		
		        },{
		            fieldLabel: 'Estimate',
		            name: 'Estimate',
		            vtype: 'Float'		
		        },{
		            fieldLabel: 'Importance',
		            name: 'Importance',
		            vtype: 'Number'		
		        }, {
		        	fieldLabel: 'Notes',
		            xtype: 'textarea',
		            name: 'Notes',
		            height:200
		        }, tagTriggerField_forEdit, tagIDTextField_forEdit
		         , {
		        	fieldLabel: 'How To Demo',
		            xtype: 'textarea',
		            name: 'HowToDemo',
		            height:200
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
		ezScrum.EditStoryForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
	},
	onRender:function() {
		ezScrum.EditStoryForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
	// Edit story action 
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var obj = this;
		var form = this.getForm();
		
		tagMenu_forEdit.items.each(function(){
			obj.updateStoryTag(this.tagId, this.text, this.checked);
		});

		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onEditSuccess(response);},
			failure:function(response){obj.onEditFailure(response);},
			params:form.getValues()
		});
	},
	updateStoryTag : function(tagId, text, checked){
		var recordTags = this.record.data['Tag'].split(',');
		var storyid = this.record.data['Id'];
		var tagExist = false;
		
		for(var i = 0; i < recordTags.length; i++){
			if(text == recordTags[i]){
				tagExist = true;
				i = recordTags.length;
			}
		}
		
		if(tagExist == true && checked == true){}
		else if(tagExist == true && checked == false){
			Ext.Ajax.request({
				url : 'AjaxRemoveStoryTag.do',
				success : function(response){},
				params : {storyId: storyid, tagId: tagId}
			});
		}
		else if(tagExist == false && checked == true){
			Ext.Ajax.request({
				url : 'AjaxAddStoryTag.do',
				success : function(response){},
				params : {storyId: storyid, tagId: tagId}
			});
		}
		else if(tagExist == false && checked == false){}
		
    },
	// Load story success
	onLoadSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = myReader.readRecords(response.responseXML);
			if(rs.success) {
				var record = rs.records[0];
				if(record)
				{
					tagMenu_forEdit.setStoryRecord(record);
					//確保 record 在 TagStore reload 之前 set 好, setCheck 才不會有問題
					CreateStoryWidgetTagStore_forEdit.reload();
					this.record = record;
					this.getForm().setValues({
						issueID:record.data['Id'],
						Name : record.data['Name'],
						Value : record.data['Value'],
						Importance : record.data['Importance'],
						Estimate : record.data['Estimate'],
						Notes : record.data['Notes'],
						HowToDemo : record.data['HowToDemo']});
					this.fireEvent('LoadSuccess', this, response, record);
				}
			}
		}
	},
	onLoadFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('LoadFailure', this, response, this.issueId);
	},
	// Update story success
	onEditSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = jsonStoryReader.read(response);
			if(rs.success) {
				var record = rs.records[0];
				if(record)
				{
					this.fireEvent('EditSuccess', this, response, record);
				}
			} else {
				this.fireEvent('EditFailure', this, response, this.issueId);
			}
		}
	},
	// Update story failure
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
			params : {issueID : this.issueId}
		});
	},
	reset:function()
	{
		this.getForm().reset();
	}
});
Ext.reg('editStoryForm', ezScrum.EditStoryForm);

ezScrum.EditStoryWidget = Ext.extend(Ext.Window, {
	title:'Edit Story',
	width:700,
	constrain:true,
	modal:true,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'editStoryForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditStoryWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
		
		this.items.get(0).on('LoadSuccess', function(obj, response, record){ this.fireEvent('LoadSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('LoadFailure', function(obj, response, issueId){ this.fireEvent('LoadFailure', this, obj, response, issueId); }, this);
		this.items.get(0).on('EditSuccess', function(obj, response, record){ this.fireEvent('EditSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('EditFailure', function(obj, response, issueId){ this.fireEvent('EditFailure', this, obj, response, issueId); }, this);
	},
	loadEditStory:function(issueId){
		this.items.get(0).issueId = issueId;
		this.items.get(0).reset();
		this.show();
		this.items.get(0).loadStore();
	}
});