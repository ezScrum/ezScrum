Ext.ns('ezScrum');

/*-----------------------------------------------------------
 *	取得Tag列表，儲存到Tag Store中
 -------------------------------------------------------------*/
var CreateStoryWidgetTagStore = new Ext.data.Store({
	fields : [ {
		name : 'Id',
		type : 'int'
	}, {
		name : 'Name'
	} ],
	reader : tagReader,
	url : 'AjaxGetTagList.do',
	autoLoad : true
});

/*-----------------------------------------------------------
 *  TagStore更新完畢之後，自動更新TagMenu
 -------------------------------------------------------------*/
CreateStoryWidgetTagStore.on('load', function(store, records, options) {
	tagMenu.removeAll();
	var tagCount = CreateStoryWidgetTagStore.getTotalCount();
	for (var i = 0; i < tagCount; i++) {
		var tagRecord = CreateStoryWidgetTagStore.getAt(i);
		tagMenu.add({
			tagId : tagRecord.data['Id'],
			text : tagRecord.data['Name'],
			xtype : 'menucheckitem',
			hideOnClick : false,
			checkHandler : tagMenu.onCheckItemClick
		});
	}
});

var tagMenu = new Ext.menu.Menu({
	/*-----------------------------------------------------------
	 *   當CheckItem被點選之後，更新TagTriggerField上的文字
	-------------------------------------------------------------*/
	onCheckItemClick : function(item, checked) {
		var tagRaw = tagTriggerField.getValue();
		var tagIDRaw = tagIDTextField.getValue();
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
		tagTriggerField.setValue(tags.join(","));
		tagIDTextField.setValue(tagIDs.join(","));
	}
});

var tagTriggerField = new Ext.form.TriggerField({
	fieldLabel : 'Tags',
	name : 'Tags',
	editable : false
});

var tagIDTextField = new Ext.form.TextField({
	xtype : 'textarea',
	name : 'TagIDs',
	hidden : true
});

tagTriggerField.onTriggerClick = function() {
	tagMenu.showAt(tagTriggerField.getPosition());
};

/* Create Story Form */
ezScrum.CreateStoryForm = Ext.extend(Ext.form.FormPanel, {
    bodyStyle     : 'padding:15px',
    border        : false,
    defaultType   : 'textfield',
    labelAlign    : 'right',
    labelWidth    : 100,
    defaults      : {
        width     : 500,
        msgTarget : 'side'
    },
    monitorValid  : true,
    initComponent : function()
    {
        var config = {
            url     : 'ajaxAddNewStory.do',
            items   : [{
                        fieldLabel : 'Name',
                        name       : 'Name',
                        allowBlank : false,
                        maxLength  : 128
                    },{
                        fieldLabel : 'Value',
                        name       : 'Value',
                        vtype      : 'Number'
                    }, {
                        fieldLabel : 'Estimate',
                        name       : 'Estimate',
                        vtype      : 'Float'
                    }, {
                        fieldLabel : 'Importance',
                        name       : 'Importance',
                        vtype      : 'Number'
                    }, {
                        fieldLabel : 'Notes',
                        xtype      : 'textarea',
                        name       : 'Notes',
                        height     : 200
                    }, tagTriggerField, tagIDTextField, {
                        fieldLabel : 'How To Demo',
                        xtype      : 'textarea',
                        name       : 'HowToDemo',
                        height     : 200
                    }, {
                        name   : 'SprintId',
                        hidden : true
                    }],
            buttons : [{
                        formBind : true,
                        text     : 'Submit',
                        scope    : this,
                        handler  : this.submit,
                        disabled : true
                    }, {
                        text    : 'Cancel',
                        scope   : this,
                        handler : function()
                        {
                            this.ownerCt.hide();
                        }
                    }]
        }

        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.CreateStoryForm.superclass.initComponent.apply(this, arguments);

        this.addEvents('CreateSuccess', 'CreateFailure');
    },
    onRender : function()
    {
        ezScrum.CreateStoryForm.superclass.onRender.apply(this, arguments);
        this.getForm().waitMsgTarget = this.getEl();
    },
    submit : function()
    {
        var myMask = new Ext.LoadMask(this.getEl(), {msg : "Please wait..."});
        myMask.show();
        var form = this.getForm();

        var obj = this;
        Ext.Ajax.request({
                    url     : this.url,
                    success : function(response)
                    {
                        obj.onSuccess(response);
                    },
                    failure : function(response)
                    {
                        obj.onFailure(response);
                    },
                    params  : form.getValues()
                });
    },
    onSuccess : function(response)
    {
        var myMask = new Ext.LoadMask(this.getEl(), {msg : "Please wait..."});
        myMask.hide();

        // check action permission
        ConfirmWidget.loadData(response);
        if (ConfirmWidget.confirmAction()) {
            var rs = jsonStoryReader.read(response);
            if (rs.success) {
                var record = rs.records[0];
                if (record)
                {
                    this.fireEvent('CreateSuccess', this, response, record);
                }

            } else {
               
                 this.fireEvent('CreateFailure', this, response);
            }
        }
    },
    onFailure : function(response)
    {
        var myMask = new Ext.LoadMask(this.getEl(), {
        	msg : "Please wait..."
        });
        myMask.hide();
        this.fireEvent('CreateFailure', this, response);
    },
    reset : function()
    {
        this.getForm().reset();

        /*-----------------------------------------------------------
         *  Reload Tag
        -------------------------------------------------------------*/
        CreateStoryWidgetTagStore.reload();
    }
});

Ext.reg('createStoryForm', ezScrum.CreateStoryForm);

ezScrum.AddNewStoryWidget = Ext.extend(Ext.Window, {
	title : 'Add New Story',
	width : 700,
	modal : true,
	closeAction : 'hide',
	constrain : true,
	initComponent : function() {
		var config = {
			layout : 'form',
			items : [ {
				xtype : 'createStoryForm'
			} ]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddNewStoryWidget.superclass.initComponent.apply(this,
				arguments);
		

		this.addEvents('CreateSuccess', 'CreateFailure');

		this.items.get(0).on('CreateSuccess', function(obj, response, record) {
			this.fireEvent('CreateSuccess', this, obj, response, record);
		}, this);
		this.items.get(0).on('CreateFailure', function(obj, response) {
			this.fireEvent('CreateFailure', this, obj, response, issueId);
		}, this);
	},
	showWidget : function() {
		this.items.get(0).reset();
		this.show();
	},
	showWidget : function(sprintID) {
		this.items.get(0).reset();
		this.items.get(0).getForm().setValues({
			SprintId : sprintID
		});
		this.show();
	}
});