Ext.ns('ezScrum')

var formPanelIDArray = new Array();

var saveBtnHandler = function(btn) {
	function showResult(btn,text){
	    if( btn == 'yes'){
	    	Ext.Ajax.request({
	      	    loadMask: true,
	      	    url: 'project/setProjectPluginConfig',
	      	    params: {
	      	    	pluginConfigJSONArrayString : JSON.stringify(pluginArray)
	      	    },
	      	    success: function(resp) {
	      	    	var projectName = resp.responseText;
	      	    	location.replace("./viewProject.do?PID=" + projectName );
	      	   } 
	      	});
	    }
	};
    var formPanel;
    var pluginArray=[];
    for( var i in formPanelIDArray ){
  	    formPanel = Ext.getCmp( formPanelIDArray[i] );//according to formPanelID to get FormPanel
  	    if( formPanel != undefined ){
  	    	//,'fieldValueMap':{}
  	    	var plugin = {'id':'','available':''};
  	  		var fieldArray = formPanel.getForm().getValues();
  	  		plugin.id = formPanel.id;
  	  		if( fieldArray['availableCheckbox'] == 'on' ){
  	  			plugin.available = 'true';
  	  		}else{
  	  			plugin.available = 'false';
  	  		}
//  	  		for( var i in fieldArray ){
//  	  			if( i != 'availableCheckbox' ){
//  	  				plugin.fieldValueMap[ i ] = fieldArray[i];
//  	  			}
//  	  		}
  	  		pluginArray.push( plugin );
  	    }
  	}
//    Ext.MessageBox.confirm('Confirm', 'plugin will work after go to project summary, are you sure?', showResult );
    Ext.MessageBox.confirm('Confirm', 'Your change will be applied, please go to page of Product Backlog.', showResult );
}


var saveBtn = new Ext.Button({
    text    : 'Save',
    handler : saveBtnHandler
});

//這是一個proxy主要是希望能夠動態地決定ezScrum.test中有哪些plugin
ezScrum.PluginConfigPage  = Ext.extend(Ext.Panel,{
	id			: 'test',
	layout		: 'anchor',
	autoScroll	: true,
	_pluginConfigArray: '',
	initComponent : function(){
		Ext.apply(this, Ext.apply(this.initialConfig));
		ezScrum.PluginConfigPage.superclass.initComponent.apply(this, arguments);
	},listeners : {
	      'beforerender' : function() {
	    	  var obj = this;
	  	      Ext.Ajax.request({
	  	    	    loadMask: true,
	  	    	    url: 'getConfigPluginList',
	  	    	    result: '',
	  	    	    success: function(resp) {
	  	    	    	// resp is the XmlHttpRequest object
	  	    	    	result = Ext.decode(resp.responseText);

	  	    	   },
	  	    	   callback: function(opt,success,resp){
	  	    	    	for ( var i in result.data.plugin){
	  	    	    		if( result.data.plugin[i].name != "" ){
	  	    	    			formPanelIDArray[i] = result.data.plugin[i].name;
	  	    	    			var formPanel = new Ext.form.FormPanel({  
	  	    	    				id: result.data.plugin[i].name,
	  	    	    				title:result.data.plugin[i].name,
	  	    	    				items:[{
	  	    			            	xtype: "fieldset",  
	  	    			            	checkboxToggle: true,  
	  	    			            	checkboxName: "availableCheckbox", 
	  	    			            	collapsed: true,
	  	    			            	title: "available",  
	  	    			            	defaultType: 'textfield',  
	  	    			            	autoWidth: true,  
	  	    			            	autoHeight: true,
	  	    			            	plugins:[result.data.plugin[i].name]
	  	    			            }]
	  	    	    			})
	  	    	    			obj.on({
	  	    	    				'show':function(){
	  	    	    					formPanel.items.get(0).plugins[0].fireEvent('show');//fire event to plugin
	  	    	    				}
	  	    	    			});
	  	    	    			obj.add( formPanel );

	  	    	    		}
	  	    	    	}
	  	    	       obj.add(saveBtn);
	  	    	   } 
	  	    	});
	  	        
	  	   },
		   'show':function(){
			   this.setData();
		   
		   },
		   'add':function(){
			   this.setData();
		   }
	  },setData : function( event ){
		  var obj = this;
 			Ext.Ajax.request({
				loadMask: true,
				url: 'project/getProjectPluginConfig',
				success: function(resp) {
					// resp is the XmlHttpRequest object
					var result = Ext.decode(resp.responseText);
					_pluginConfigArray = result.data.pluginConfigArray;
					for( var i in _pluginConfigArray ){
						var pluginConfig = _pluginConfigArray[i];
						var form;
						for( var key in pluginConfig ){
							if( key == 'id' ){
								form = Ext.getCmp( pluginConfig.id ).getForm();
							}else if( key == 'available'){
								if( pluginConfig[key] == true ){
									if( Ext.getCmp( pluginConfig.id ).items.get(0).checkbox ){
								        Ext.getCmp( pluginConfig.id ).items.get(0).expand();
									}
								}
							}
//							else if( key == 'fieldValueMap' ){
//								for( var field in pluginConfig[key] ){
//									if( form.findField(field) != undefined ){
//									    form.findField(field).setValue( pluginConfig[key][field] );	
//									}
//								}
//							}
							
  						
						}

					}	
				} 
			});
	  }
});
var PluginConfigPage = new ezScrum.PluginConfigPage();