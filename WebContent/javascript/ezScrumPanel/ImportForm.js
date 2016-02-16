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
	monitorValid : true,
	loadmask : null,
	initComponent : function() {
		var obj = this;
		var config = {
			url : '/ezScrum/resource/dataMigration/projects',
			items : [ {
				id: "files",
				xtype: 'fileuploadfield',
				emptyText: 'Select a document to upload...',
                fieldLabel: 'File',
				buttonText: 'Browse',
				acceptMimes: ['json'],
				listeners: {
					'fileselected' : function(fb, v) {
						obj.items.get(1).setDisabled(false);
					}
				}
			},{
				xtype: 'button',
				formBind : true,
				scope : this,
				text : 'Import',
				disabled : true,
				width: 300,
				handler : this.import
			}],	
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ImportFormLayout.superclass.initComponent.apply(this, arguments);
	},
	import : function() {
		var MD5EncodingAdmin = '21232f297a57a5a743894a0e4a801fc3';
		Ext.MessageBox.getDialog().body.child('input').dom.type='password';
		Ext.MessageBox.prompt('Identity verification', "Please enter the administrator's password:",
            function(btn, password) {
				var MD5EncodingPassword = md5(password);
	            if (btn == 'ok') {
	            	// check is admin want to import file
	        		Ext.MessageBox
	        				.show({
	        					title : 'Confirm to Import?',
	        					msg : 'Watch out! </br></br> If import data conflict with local data!<br>(Project)<br>The project will be renamed.<br>(Account)<br>Local account data will be reserved.',
	        					buttons : Ext.MessageBox.YESNO,
	        					icon : Ext.MessageBox.QUESTION,
	        					fn : function(btn) {
	        						if (btn == 'yes') {
	        							// get golden answer file content!!
	        							var reader = new FileReader();
	        							reader.readAsText($("input[type='file']")[0].files[0]);
	        							// file content is in
	        							// e.target.result
	        							reader.onloadend = function(e) {
	        								Ext.MessageBox.show({
	        									  msg: 'Importing your data, please wait...',
	        									  progressText: 'Importing...',
	        									  width:300,
	        									  wait:true,
	        									  waitConfig: {interval:200}
	        									});
	        								$.ajax(
	        										{
	        										   url : "/ezScrum/resource/dataMigration/projects",
	        										   headers: {
	        										        'username': MD5EncodingAdmin,
	        										        'password': MD5EncodingPassword
	        										   },
	        										   method : "post",
	        										   data : e.target.result
	        										})
	        								.done(function(msg) {
	        									Ext.Msg.alert('Status', 'Project Import successfully.');
	        								}).fail(function(err) {
	        									Ext.Msg.alert('Status', 'Project Import fail. </br> ' + err);
	        						});
	        					};
	        		  }
	        		}
	        	   });
	            }
            }
		);
	}
});

Ext.reg('ImportForm', ImportFormLayout);