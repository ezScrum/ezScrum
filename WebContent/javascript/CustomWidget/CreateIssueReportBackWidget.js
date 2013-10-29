Ext.ns('ezScrum');


/* project data store */
var ProjectComboStore = new Ext.data.Store({
    id				: 0,
    fields			: [{name: 'Name'}],
    reader			: ProjectReader    
});

/* project combo box */
var ProjectComboBox = new Ext.form.ComboBox({
			allowBlank 		: false,
		    typeAhead		: true,
		    triggerAction	: 'all',
		    lazyRender		: true,
		    editable		: false,
		    mode			: 'local',
		    store			: ProjectComboStore,
		    name			: 'projectComboBox',
		    emptyText		: 'Please choose a project name..',
		    valueField		: 'Name',
		    displayField	: 'Name',
		    fieldLabel		: 'Projects',
		    id				: 'projectComboBox',
		    anchor    		: '95%',
		    listeners		: {
				'select'	: GetCategory
			}
});

/* category Reader */
var CategoryReader = new Ext.data.XmlReader({
	record			: 'Categories',
	idPath			: 'Name',
	successProperty	: 'Result'	
}, Common);

/* category data store */
var CategoryComboStore = new Ext.data.Store({
    id				: 0,
    fields			: [{name: 'Name'}],
    reader			: CategoryReader
});

/* category combo box */
var CategoryComboBox = new Ext.form.ComboBox({
			allowBlank 		: false,
		    typeAhead		: true,
		    triggerAction	: 'all',
		    lazyRender		: true,
		    autoLoad		: 'false',
		    editable		: false,
		    mode			: 'local',
		    store			: CategoryComboStore,
		    name			: 'categoryComboBox',
		    emptyText		: 'Please choose a project first..',
		    valueField		: 'Name',
		    displayField	: 'Name',
		    fieldLabel		: 'Categories',
		    id				: 'categoryComboBox',
		    forceSelection	: true,
		    anchor    		: '95%' 
});

/* issue name */     
var IssueName = new Ext.form.TextField({
            fieldLabel	: 'Issue Name',
            name      	: 'IssueName',
            allowBlank	: false,
            anchor    	: '95%',
            autoScroll	: true
});

/* issue description */     
var IssueDesc = new Ext.form.TextArea({
            fieldLabel	: 'Issue Description',
            name      	: 'IssueDesc',
            allowBlank	: false,
            anchor    	: '95% 50%',
            autoScroll	: true
});

/* user name */		
var UserName = new Ext.form.TextField({
			fieldLabel	: 'Your Name',
            name      	: 'UserName',
            allowBlank	: false,
            anchor    	: '95%'
});

/* user mail */
var UserMail = new Ext.form.TextField({
			fieldLabel	: 'Your Mail',
            name      	: 'UserMail',
            allowBlank	: false,
            anchor    	: '95%'
});

/* form */
ezScrum.IssueReportBackForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle		: 'padding:15px',
	border			: false,
	defaultType		: 'textfield',
	labelAlign		: 'right',
	labelWidth		: 150,
	defaults: {
        width		: 350,
        msgTarget	: 'side'
    },
    monitorValid	: true,
    initComponent	: function() {
    	var config = {
    		url		: './AjaxCreateIssueReprot.do',
    		fileUpload	: true,
			allowBlank	: false,
			items	: [	ProjectComboBox, 
						CategoryComboBox, 
						IssueName, 
						IssueDesc, 
						UserName, 
						UserMail, 
						{		// Upload file field
							xtype		: 'fileuploadfield',
							emptyText	: 'Select a file..',
		            		fieldLabel	: 'Upload File',
		            		name		: 'file',
		            		anchor		: '95%'
		        		}
						],
		    buttons	: [{
			    	formBind	: true,
		    		text		: 'Submit',
		    		scope		: this,
		    		handler		: this.submit,
		    		disabled	: true
	    		},
	    		{
		        	text		: 'Cancel',
		        	scope		: this,
		        	handler		: function() {this.ownerCt.hide();}
	       		}]
    	}
    	
	    Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.IssueReportBackForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
    },
    onRender:function() {
		ezScrum.IssueReportBackForm.superclass.onRender.apply(this, arguments);
	},
    submit			: function() {
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..." });
		myMask.show();	
		
		var obj = this;
		var readUrl = this.url;
		var form = this.getForm().submit({
			url		: readUrl,
			success	: function(response) { obj.onSuccess(response); },
			failure	: function(response) { obj.onFailure(response); }
		});
	},
	onSuccess		: function(response) {
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});		
		myMask.hide();
		
		this.fireEvent('CreateSuccess', this, response);
	},
	onFailure		: function(response) {
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		this.fireEvent('CreateFailure', this, response);
	},
	reset			: function() {
		this.getForm().reset();
	}       
});
Ext.reg('IssueReportBackForm', ezScrum.IssueReportBackForm);

/* widget */
ezScrum.ShowIssueReportBackWidget = Ext.extend(Ext.Window, {
    title           : 'Issue Report Back',
    width           : 600,
    modal           : true,
    closeAction     : 'hide',
    initComponent	: function() {
		var config = {
			layout	: 'form',
			items	: [{xtype: 'IssueReportBackForm'}]
        }
        Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ShowIssueReportBackWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('ShowSuccess', 'ShowFailure');
		this.addEvents('CreateSuccess', 'CreateFailure');
		
		this.items.get(0).on('ShowSuccess', function(obj, response){ this.fireEvent('ShowSuccess'); }, this);
		this.items.get(0).on('ShowFailure', function(obj, response){ this.fireEvent('ShowFailure'); }, this);
		this.items.get(0).on('CreateSuccess', function(obj, response){ this.fireEvent('CreateSuccess'); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response){ this.fireEvent('CreateFailure'); }, this);
    },
	showWidget		: function() {
		var widget = this;
		widget.items.get(0).reset();
		Ext.Ajax.request({
			url			: './AjaxShowIssueReportBack.do',
			success		: function(response) {
				ProjectComboStore.loadData(response.responseXML);
				var ResultValue = ProjectReader.read(response);
				
				if (ResultValue.success) {
					widget.fireEvent('ShowSuccess');
				} else {
					widget.fireEvent('ShowFailure');
				}
			}
		});
	}	
});

// request to get Project's categories
function GetCategory() {
	// clean categories label's information
	CategoryComboBox.reset();
	
	// according the ProjectID to set the new catagories
	var ProjectID = ProjectComboBox.getValue();
	AjaxRequestGetCategory('./AjaxShowIssueReportBack.do', ProjectID);
}

// Ajax get Project's Categories Info
function AjaxRequestGetCategory(url, P_ID) {
	Ext.Ajax.request({
		url			: url,
		params		: {ProjectID : P_ID},
		success		: function(response) {
			CategoryComboStore.loadData(response.responseXML);
		}
	});
}