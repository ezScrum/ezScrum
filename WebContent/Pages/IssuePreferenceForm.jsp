<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<script type="text/javascript" src="javascript/ezScrumDataModel/ITSConfigDescription.js"></script>

<script type="text/javascript">
	Ext.onReady(function() {
		var ITPreForm = new Ext.FormPanel({
	        url:'saveITSPreference.do',
	        border : true,
	        frame: false,
	        title: 'ITS Rreference',
	        bodyStyle:'padding:15px',
	        labelAlign : 'right',
			labelWidth : 150,
	        monitorValid:true,
	        items: [ITSConfigItem]
	    });
	    
	    var submit = ITPreForm.addButton({
	    	formBind:true,
	    	disabled:true,
	        text: 'Submit',
	        handler: function(){
				var form = ITPreForm.getForm();
				Ext.Ajax.request({
					url:ITPreForm.url,
					success: function(response) {
						var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
						loadmask.hide();
						Ext.example.msg('Modify ITS Preference', 'Success.');
		    		},
		    		failure: function(response) {
		    			var loadmask = new Ext.LoadMask(this.getEl(), {msg:"loading info..."});
		    			loadmask.hide();
		    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
		    		},
					params: form.getValues()
				});
	        }
	    });
	    
	    var cancel = ITPreForm.addButton({
	        text: 'Cancel',
	        handler: function(){
	            document.location.href="<html:rewrite action="/viewProjectSummary" />";
	        }
	    });
	    
	
	    ITPreForm.render('content');
	    
	    Ext.Ajax.request({
			url:'showITSPreference.do',
			success: function(response){
				ITSConfigStore.loadData(Ext.decode(response.responseText));
				var record = ITSConfigStore.getAt(0);
				ITPreForm.getForm().setValues({
					ServerUrl: record.get('ServerUrl'),
					ITSAccount: record.get('ITSAccount'), 
					ITSPassword: ''
				});
			},
			failure:function(response){
				alert('Failure');
			}
		});
	})

</script>
<div id = "content">
</div>
<div id="SideShowItem" style="display:none;">showITSPreferenceForm</div>
