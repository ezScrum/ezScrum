ImportFormLayout = Ext.extend(Ext.form.FormPanel, {
	id : 'Import_Form',
	border : false,
	frame : true,
	title : 'Import Page',
	padding : '0 0 0 100',
	labelAlign : 'center',
	labelWidth : 80,
	labelStyle: 'margin-left:100px;',
	defaults: {
        anchor: '-100',
        allowBlank: false,
        msgTarget: 'side'
    },
//	buttonAlign : 'center',
	monitorValid : true,
	loadmask : null,
	initComponent : function() {
		var config = {
			url : 'import.do',
			modify_url : 'import.do',
			items : [ {
				id:"files",
				xtype : 'fileuploadfield',
				emptyText: 'Select a document to upload...',
                fieldLabel: 'File',
//                anchor: '-200',
                buttonText: 'Browse'
			},{
				xtype: 'button',
				formBind : true,
				scope : this,
				text : 'Import',
//				disabled : true,
				width: 300,
				handler : this.import
			} ],	
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ImportFormLayout.superclass.initComponent.apply(this, arguments);
	},
	import : function() {
		//get golden answer file content!!
		var reader = new FileReader();
		reader.readAsText($("input[type='file']")[0].files[0]);
		//file content is in e.target.result
		reader.onloadend = function(e) {
			console.log(e.target.result);
		    $.ajax({
				url: "",
				method: "post",
				data:e.target.result
			}).done(function(msg){
				console.log(msg);
			}).fail(function(err){
				console.error(err);
			});
		};
	}
});

Ext.reg('ImportForm', ImportFormLayout);